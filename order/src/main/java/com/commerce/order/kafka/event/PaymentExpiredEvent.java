package com.commerce.order.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/11/4 15:27
 * @package com.commerce.order.kafka.event
 * <p>
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentExpiredEvent implements Serializable {
    private Long orderId;
    private Long paymentId;
    private LocalDateTime expiredAt;
    private LocalDateTime eventTime;
}
