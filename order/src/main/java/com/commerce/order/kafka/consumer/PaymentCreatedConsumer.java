package com.commerce.order.kafka.consumer;

import com.commerce.order.kafka.event.PaymentCreatedEvent;
import com.commerce.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author Yixi Wan
 * @date 2025/11/4 14:10
 * @package com.commerce.order.kafka
 * <p>
 * Description:
 */
@Component
@Slf4j
public class PaymentCreatedConsumer {

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "payment-created", groupId = "order-service-group")
    public void handlePaymentCreated(PaymentCreatedEvent event) {
        log.info("[Kafka] Received PaymentCreatedEvent → orderId={}, paymentId={}", event.getOrderId(), event.getPaymentId());
        try {
            orderService.updatePaymentId(event.getOrderId(), event.getPaymentId());
        } catch (Exception e) {
            log.error("[Order] Failed to update paymentId for orderId={}: {}", event.getOrderId(), e.getMessage());
        }
    }
}
