package com.commerce.product.clients;

import lombok.Data;

/**
 * @author Yixi Wan
 * @date 2025/11/3 11:22
 * @package com.commerce.inventory.dto
 * <p>
 * Description:
 */
@Data
public class InventoryResponse {
    private Long productId;
    private Integer availableStock;
    private Integer lockedStock;
    private Integer soldStock;
    private String lastUpdated;
}
