package com.commerce.payment.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:17
 * @package com.commerce.payment.kafka
 * <p>
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent implements Serializable {
    private Long orderId;
    private Long paymentId;
    private String paymentStatus; // INITIATED, SUCCESS, FAILED
    private Double amount;
    private LocalDateTime eventTime;
}
