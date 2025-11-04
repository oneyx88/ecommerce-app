package com.commerce.inventory.dto;

import lombok.Data;

/**
 * @author Yixi Wan
 * @date 2025/11/3 11:21
 * @package com.commerce.inventory.dto
 * <p>
 * Description:
 */
@Data
public class InventoryRequest {
    private Integer availableStock;
    private Integer lockedStock;
    private Integer soldStock;
}
