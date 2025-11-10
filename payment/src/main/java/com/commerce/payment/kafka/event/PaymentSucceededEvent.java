package com.commerce.payment.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/11/4 13:41
 * @package com.commerce.payment.kafka
 * <p>
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSucceededEvent implements Serializable {
    private Long orderId;
    private Long paymentId;
    private Double amount;
    private String paymentMethod;
//    private String transactionRef; // 第三方流水号
    private LocalDateTime paidAt;
}
