package com.commerce.order.kafka.consumer;

import com.commerce.order.kafka.event.PaymentFailedEvent;
import com.commerce.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author Yixi Wan
 * @date 2025/11/4 14:30
 * @package com.commerce.order.kafka.consumer
 * <p>
 * Description:
 */
@Component
@Slf4j
public class PaymentFailedConsumer {

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "payment-failed", groupId = "order-service-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("[Kafka] PaymentFailedEvent → orderId={}, reason={}", event.getOrderId(), event.getReason());
    }
}

