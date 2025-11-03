package com.commerce.product.dto.category;


import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/10/20 23:19
 * @package com.commerce.ecommapp.dto
 * <p>
 * Description:
 */
@Data
@Builder
public class PagedCategoryResponse {
    private List<CategoryResponse> categories;
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalElements;
    private boolean islastPage;
}
