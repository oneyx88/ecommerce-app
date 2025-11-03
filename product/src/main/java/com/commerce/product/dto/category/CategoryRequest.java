package com.commerce.product.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Yixi Wan
 * @date 2025/10/20 23:42
 * @package com.commerce.ecommapp.dto
 * <p>
 * Description:
 */
@Data
public class CategoryRequest {
    @NotBlank
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String categoryName;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}
