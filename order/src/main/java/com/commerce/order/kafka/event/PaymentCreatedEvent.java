package com.commerce.order.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/11/4 13:40
 * @package com.commerce.payment.kafka
 * <p>
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreatedEvent implements Serializable {
    private Long orderId;
    private Long paymentId;
    private Double amount;
    private LocalDateTime createdAt;
}

