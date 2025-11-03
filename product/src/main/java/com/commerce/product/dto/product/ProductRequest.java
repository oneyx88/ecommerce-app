package com.commerce.product.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixi Wan
 * @date 2025/10/22 12:16
 * @package com.commerce.ecommapp.dto.product
 * <p>
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String productName;
    private String description;
    private Integer quantity;
    private Double price;
    private Double discount;
}
