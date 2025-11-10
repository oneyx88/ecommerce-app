package com.commerce.order.kafka.consumer;

import com.commerce.order.kafka.event.PaymentExpiredEvent;
import com.commerce.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author Yixi Wan
 * @date 2025/11/4 15:26
 * @package com.commerce.order.kafka.consumer
 * <p>
 * Description:
 */
@Component
@Slf4j
public class PaymentExpiredConsumer {

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "payment-expired", groupId = "order-service-group")
    public void handlePaymentExpired(PaymentExpiredEvent event) {
        log.info("[Kafka] Received PaymentExpiredEvent → orderId={}", event.getOrderId());
        try {
            orderService.markOrderAsExpired(event);
        } catch (Exception e) {
            log.error("[Order] Failed to cancel expired order → orderId={}, error={}", event.getOrderId(), e.getMessage());
        }
    }
}
