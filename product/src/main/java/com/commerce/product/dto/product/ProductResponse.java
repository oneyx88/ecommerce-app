package com.commerce.product.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixi Wan
 * @date 2025/10/22 12:17
 * @package com.commerce.ecommapp.dto.product
 * <p>
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long productId;
    private String productName;
    private String description;
    private Integer availableStock;
    private String image;
    private Double price;
    private Double discount;
    private Double specialPrice;
}
