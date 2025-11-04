package com.commerce.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixi Wan
 * @date 2025/11/3 11:22
 * @package com.commerce.inventory.dto
 * <p>
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOperationRequest {
    private Long productId;
    private Integer quantity;
}