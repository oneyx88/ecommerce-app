package com.commerce.order.service;

import com.commerce.order.clients.CartClientService;
import com.commerce.order.clients.InventoryClientService;
import com.commerce.order.clients.ProductClientService;
import com.commerce.order.config.OrderEvent;
import com.commerce.order.config.OrderState;
import com.commerce.order.dto.*;
import com.commerce.order.exceptions.ApiException;
import com.commerce.order.exceptions.ResourceNotFoundException;
import com.commerce.order.kafka.event.*;
import com.commerce.order.model.Order;
import com.commerce.order.model.OrderItem;
import com.commerce.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:19
 * @package com.commerce.order.service
 * <p>
 * Description:
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CartClientService cartClientService;
    @Autowired
    private ProductClientService productClientService;
    @Autowired
    private InventoryClientService inventoryClientService;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

    // -----------------------------------------------------------------------
    // 1️⃣ 创建订单
    // -----------------------------------------------------------------------
    @Override
    @Transactional
    public OrderResponse createOrder(String keycloakId, String userEmail) {
        log.info("[Order] Creating order for keycloakId={}", keycloakId);

        // 获取购物车
        CartResponse cartResponse = cartClientService.getCartByKeyCloakId(keycloakId);
        List<CartItem> cartItems = cartResponse.getCartItems();
        if (cartItems.isEmpty()) {
            throw new ApiException("Cart is empty", HttpStatus.BAD_REQUEST);
        }

        // 校验并同步商品信息
        for (CartItem item : cartItems) {
            ProductDTO product = productClientService.getProductById(item.getProductId());
            if (product.getAvailableStock() < item.getQuantity()) {
                throw new ApiException("Insufficient stock for product: " + product.getProductName(),
                        HttpStatus.BAD_REQUEST);
            }
            item.setProductName(product.getProductName());
            item.setProductPrice(product.getPrice());
            item.setDiscount(product.getDiscount());
            item.setImage(product.getImage());
        }

        // 计算订单金额
        double totalAmount = cartItems.stream()
                .mapToDouble(i -> i.getProductPrice() * i.getQuantity())
                .sum();

        // 构建订单
        Order order = new Order();
        order.setKeycloakId(keycloakId);
        order.setEmail(userEmail);
        order.setOrderStatus(OrderState.CREATED.name());
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());

        // 构建订单项
        List<OrderItem> orderItems = cartItems.stream().map(item -> {
            OrderItem oi = new OrderItem();
            oi.setProductId(item.getProductId());
            oi.setProductName(item.getProductName());
            oi.setProductPrice(item.getProductPrice());
            oi.setDiscount(item.getDiscount());
            oi.setImage(item.getImage());
            oi.setQuantity(item.getQuantity());
            oi.setOrderedProductPrice(item.getProductPrice() * item.getQuantity());
            oi.setOrder(order);
            return oi;
        }).collect(Collectors.toList());
        order.setOrderItems(orderItems);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 锁库存
        for (CartItem item : cartItems) {
            inventoryClientService.lockStock(item.getProductId(), item.getQuantity());
        }

        // 清空购物车
        cartClientService.clearCart(keycloakId);

        // 注册事务提交后发送 Kafka 事件
        registerOrderCreatedEvent(savedOrder);

        // 构建响应
        OrderResponse response = modelMapper.map(savedOrder, OrderResponse.class);
        response.setOrderItems(orderItems.stream()
                .map(i -> modelMapper.map(i, OrderItemResponse.class))
                .collect(Collectors.toList()));
        response.setTotalAmount(totalAmount);

        log.info("[Order] Order created successfully → orderId={}", savedOrder.getOrderId());
        return response;
    }

    // -----------------------------------------------------------------------
    // 2️⃣ 绑定 PaymentId（消费 PaymentCreatedEvent）
    // -----------------------------------------------------------------------
    @Override
    @Transactional
    public void updatePaymentId(Long orderId, Long paymentId) {
        log.info("[Order] Binding paymentId={} to orderId={}", paymentId, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "OrderId", orderId));

        if (order.getPaymentId() != null && order.getPaymentId().equals(paymentId)) {
            log.info("[Order] Payment already bound → orderId={}, paymentId={}", orderId, paymentId);
            return;
        }

        if (!OrderState.CREATED.name().equals(order.getOrderStatus())) {
            log.warn("[Order] Invalid state to bind payment → orderId={}, status={}",
                    orderId, order.getOrderStatus());
            return;
        }

        order.setPaymentId(paymentId);
        orderRepository.save(order);
    }

    // -----------------------------------------------------------------------
    // 3️⃣ 支付成功（消费 PaymentSucceededEvent）
    // -----------------------------------------------------------------------
    @Override
    @Transactional
    public void markOrderAsPaid(PaymentSucceededEvent event) {
        Long orderId = event.getOrderId();
        log.info("[Order] Received PaymentSucceededEvent → orderId={}, paymentId={}",
                orderId, event.getPaymentId());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "OrderId", orderId));

        StateMachine<OrderState, OrderEvent> sm =
                stateMachineFactory.getStateMachine(order.getOrderId().toString());
        sm.start();

        // 触发状态迁移事件
        boolean accepted = sm.sendEvent(OrderEvent.PAYMENT_SUCCEEDED);

        if (!accepted) {
            log.warn("[Order] Payment success event rejected → orderId={}, currentState={}",
                    orderId, sm.getState().getId());
            return;
        }

        order.setOrderStatus(sm.getState().getId().name());
        order.setPaidAt(event.getPaidAt());
        orderRepository.save(order);

        log.info("[Order] Marked order as PAID via state machine → orderId={}", orderId);

        // 注册 afterCommit 事件发送
        registerOrderConfirmedEvent(order);
    }

    @Override
    @Transactional
    public void markOrderAsExpired(PaymentExpiredEvent event) {
        Long orderId = event.getOrderId();
        log.info("[Order] Received PaymentExpiredEvent → orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "OrderId", orderId));

        StateMachine<OrderState, OrderEvent> sm =
                stateMachineFactory.getStateMachine(order.getOrderId().toString());
        sm.start();

        boolean accepted = sm.sendEvent(OrderEvent.PAYMENT_EXPIRED);

        if (!accepted) {
            log.warn("[Order] PaymentExpiredEvent rejected by state machine → orderId={}, currentState={}",
                    orderId, sm.getState().getId());
            return;
        }

        // 更新状态
        order.setOrderStatus(sm.getState().getId().name());
        orderRepository.save(order);

        log.info("[Order] Order cancelled due to payment timeout → orderId={}", orderId);

        // 事务提交后再发“订单取消/支付过期”事件（给库存做释放）
        registerOrderCancelledEvent(order);
    }


    // -----------------------------------------------------------------------
    // 4️⃣ 注册 Kafka 事件发送（after commit）
    // -----------------------------------------------------------------------
    private void registerOrderCreatedEvent(Order savedOrder) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                OrderCreatedEvent event = buildOrderCreatedEvent(savedOrder);
                sendOrderCreatedMessage(event);
            }
        });
    }

    private void registerOrderConfirmedEvent(Order order) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                order.getOrderItems().forEach(item -> {
                    OrderConfirmedEvent event = buildOrderConfirmedEvent(order, item);
                    sendOrderConfirmedEvent(event);
                });
            }
        });
    }

    private void registerOrderCancelledEvent(Order order) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                order.getOrderItems().forEach(item -> {
                    OrderCancelledEvent event = buildOrderCancelledEvent(order, item);
                    sendOrderCancelledEvent(event);
                });
            }
        });
    }

    private OrderCreatedEvent buildOrderCreatedEvent(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getOrderId());
        event.setKeycloakId(order.getKeycloakId());
        event.setEmail(order.getEmail());
        event.setTotalAmount(order.getTotalAmount());
        event.setCurrency("USD");
        event.setCreatedAt(order.getCreatedAt());
        event.setEventTime(LocalDateTime.now());

        List<OrderItemPayload> items = order.getOrderItems().stream()
                .map(i -> new OrderItemPayload(
                        i.getProductId(),
                        i.getProductName(),
                        i.getQuantity(),
                        i.getProductPrice()
                ))
                .toList();
        event.setOrderItems(items);

        return event;
    }

    private OrderConfirmedEvent buildOrderConfirmedEvent(Order order, OrderItem item) {
        return OrderConfirmedEvent.builder()
                .orderId(order.getOrderId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .eventTime(LocalDateTime.now())
                .build();
    }

    private OrderCancelledEvent buildOrderCancelledEvent(Order order, OrderItem item) {
        return OrderCancelledEvent.builder()
                .orderId(order.getOrderId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .reason("PAYMENT_EXPIRED")
                .eventTime(LocalDateTime.now())
                .build();
    }

    private void sendOrderCreatedMessage(OrderCreatedEvent event) {
        try {
            kafkaTemplate.send("order-created", event);
            log.info("[Kafka] OrderCreatedEvent sent successfully → orderId={}", event.getOrderId());
        } catch (Exception e) {
            log.error("[Kafka] Failed to send OrderCreatedEvent → {}", e.getMessage(), e);
        }
    }

    private void sendOrderConfirmedEvent(OrderConfirmedEvent event) {
        try {
            kafkaTemplate.send("order-confirmed", event);
            log.info("[Kafka] OrderConfirmedEvent sent → orderId={}, productId={}, qty={}",
                    event.getOrderId(), event.getProductId(), event.getQuantity());
        } catch (Exception e) {
            log.error("[Kafka] Failed to send OrderConfirmedEvent → {}", e.getMessage(), e);
        }
    }

    private void sendOrderCancelledEvent(OrderCancelledEvent event) {
        try {
            kafkaTemplate.send("order-cancelled", event);
            log.info("[Kafka] OrderCancelledEvent sent → orderId={}, productId={}, qty={}, reason={}",
                    event.getOrderId(), event.getProductId(), event.getQuantity(), event.getReason());
        } catch (Exception e) {
            log.error("[Kafka] Failed to send OrderCancelledEvent → {}", e.getMessage(), e);
        }
    }

}

