package com.commerce.payment.kafka;

import com.commerce.payment.dto.PaymentRequest;
import com.commerce.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author Yixi Wan
 * @date 2025/11/3 20:48
 * @package com.commerce.payment.kafka
 * <p>
 * Description:
 */
/**
 * 消费 OrderService 发送的 “order-created” 消息
 * 由 PaymentService 自动创建支付记录
 */
@Component
@Slf4j
public class OrderCreatedConsumer {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ModelMapper modelMapper;

    @KafkaListener(topics = "order-created", groupId = "payment-service-group")
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("[Kafka] Received OrderCreatedEvent → orderId=" + event.getOrderId());

        try {
            PaymentRequest request = modelMapper.map(event, PaymentRequest.class);
            paymentService.createPayment(request);

            log.info("[Payment] Created payment for orderId=" + event.getOrderId());
        } catch (Exception e) {
            log.error("[Payment] Failed to create payment for orderId=" + event.getOrderId() + ": " + e.getMessage());
        }
    }
}

