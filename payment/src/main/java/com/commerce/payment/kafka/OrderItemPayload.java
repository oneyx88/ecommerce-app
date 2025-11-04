package com.commerce.payment.kafka;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:29
 * @package com.commerce.order.kafka
 * <p>
 * Description:
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单子项简化快照（用于事件）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemPayload implements Serializable {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double orderedProductPrice;
}
