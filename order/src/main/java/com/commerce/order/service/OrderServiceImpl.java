package com.commerce.order.service;

import com.commerce.order.clients.CartClientService;
import com.commerce.order.clients.InventoryClientService;
import com.commerce.order.clients.ProductClientService;
import com.commerce.order.dto.*;
import com.commerce.order.exceptions.ApiException;
import com.commerce.order.kafka.OrderCreatedEvent;
import com.commerce.order.kafka.OrderItemPayload;
import com.commerce.order.model.Order;
import com.commerce.order.model.OrderItem;
import com.commerce.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
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
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Override
    @Transactional
    public OrderResponse createOrder(String keycloakId, String userEmail) {
        // 1️⃣ 获取用户购物车
        CartResponse cartResponse = cartClientService.getCartByKeyCloakId(keycloakId);
        List<CartItem> cartItems = cartResponse.getCartItems();

        // 2️⃣ 校验并同步商品信息
        for (CartItem item : cartItems) {
            ProductDTO product = productClientService.getProductById(item.getProductId());
            if (product.getAvailableStock() < item.getQuantity()) {
                throw new ApiException("Insufficient stock for product: " + product.getProductName(), HttpStatus.BAD_REQUEST);
            }
            item.setProductName(product.getProductName());
            item.setProductPrice(product.getPrice());
            item.setDiscount(product.getDiscount());
            item.setImage(product.getImage());
        }

        // 3️⃣ 计算订单金额
        double totalAmount = cartItems.stream()
                .mapToDouble(i -> (i.getProductPrice() - i.getDiscount()) * i.getQuantity())
                .sum();

        // 4️⃣ 创建订单实体
        Order order = new Order();
        order.setKeycloakId(keycloakId);
        order.setEmail(userEmail);
        order.setOrderStatus("CREATED");
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());

        // 5️⃣ 创建订单项（快照）
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

        // 6️⃣ 保存订单
        Order savedOrder = orderRepository.save(order);

        // 7️⃣ 锁库存
        for (CartItem item : cartItems) {
            inventoryClientService.lockStock(item.getProductId(), item.getQuantity());
        }

        // 8️⃣ 清空购物车
        cartClientService.clearCart(keycloakId);

        // 9️⃣ 注册 Kafka 消息发送（事务提交后再发送）
        registerOrderCreatedEvent(savedOrder);

        // 🔟 生成响应 DTO
        OrderResponse response = modelMapper.map(savedOrder, OrderResponse.class);
        response.setOrderItems(orderItems.stream()
                .map(i -> modelMapper.map(i, OrderItemResponse.class))
                .collect(Collectors.toList()));
        response.setTotalAmount(totalAmount);

        return response;
    }

    /**
     * 注册事务同步，在提交后发送 Kafka 消息
     */
    private void registerOrderCreatedEvent(Order savedOrder) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                OrderCreatedEvent event = buildOrderCreatedEvent(savedOrder);
                sendOrderCreatedMessage(event);
            }
        });
    }

    /**
     * todo 根据第三方Gateway实现
     * 构建 Kafka 事件对象
     */
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

    /**
     * 实际发送 Kafka 消息
     */
    private void sendOrderCreatedMessage(OrderCreatedEvent event) {
        try {
            kafkaTemplate.send("order-created", event);
            log.info("[Kafka] OrderCreatedEvent sent successfully → " + event.getOrderId());
        } catch (Exception e) {
            log.error("[Kafka] Failed to send OrderCreatedEvent: " + e.getMessage());
        }
    }
}

