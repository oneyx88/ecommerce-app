package com.commerce.product.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixi Wan
 * @date 2025/10/21 20:00
 * @package com.commerce.ecommapp.dto
 * <p>
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String categoryName;
    private String description;
}
