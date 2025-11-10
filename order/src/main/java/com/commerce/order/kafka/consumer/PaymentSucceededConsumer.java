package com.commerce.order.kafka.consumer;

import com.commerce.order.kafka.event.PaymentSucceededEvent;
import com.commerce.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author Yixi Wan
 * @date 2025/11/4 14:18
 * @package com.commerce.order.kafka.consumer
 * <p>
 * Description:
 */
@Component
@Slf4j
public class PaymentSucceededConsumer {

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "payment-succeeded", groupId = "order-service-group")
    public void handlePaymentSucceeded(PaymentSucceededEvent event) {
        log.info("[Kafka] PaymentSucceededEvent → orderId={}, paymentId={}", event.getOrderId(), event.getPaymentId());
        try {
            orderService.markOrderAsPaid(event);
        } catch (Exception e) {
            log.error("[Order] Failed to mark order as PAID → orderId={}", event.getOrderId(), e);
        }
    }
}

