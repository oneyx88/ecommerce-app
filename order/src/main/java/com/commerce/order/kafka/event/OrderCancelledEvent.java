package com.commerce.order.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/11/5 23:39
 * @package com.commerce.order.kafka.event
 * <p>
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelledEvent implements Serializable {
    private Long orderId;             // 订单ID
    private Long productId;           // 商品ID
    private Integer quantity;         // 数量
    private String reason;            // 取消原因（如 PAYMENT_EXPIRED）
    private LocalDateTime eventTime;  // 事件时间
}
