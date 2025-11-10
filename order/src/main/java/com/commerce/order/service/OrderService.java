package com.commerce.order.service;

import com.commerce.order.dto.OrderResponse;
import com.commerce.order.kafka.event.PaymentExpiredEvent;
import com.commerce.order.kafka.event.PaymentFailedEvent;
import com.commerce.order.kafka.event.PaymentSucceededEvent;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:07
 * @package com.commerce.order.service
 * <p>
 * Description:
 */
public interface OrderService {
    OrderResponse createOrder(String keycloakId, String userEmail);

    void updatePaymentId(Long orderId, Long paymentId);

    void markOrderAsPaid(PaymentSucceededEvent event);

    void markOrderAsExpired(PaymentExpiredEvent event);
}
