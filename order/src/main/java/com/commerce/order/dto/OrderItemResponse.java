package com.commerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:12
 * @package com.commerce.order.dto
 * <p>
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    private Long orderItemId;
    private Long productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double discount;
    private Double orderedProductPrice; // quantity * price - discount
}
