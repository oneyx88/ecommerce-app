package com.commerce.product.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Yixi Wan
 * @date 2025/11/4 19:52
 * @package com.commerce.product.dto.product
 * <p>
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummary implements Serializable {

    private Long productId;      // 商品ID
    private String productName;  // 商品名
    private String image;        // 商品主图
    private Double price;        // 原价
    private Double discount;     // 折扣率（可选）
    private Double specialPrice; // 折后价（展示价）
}
