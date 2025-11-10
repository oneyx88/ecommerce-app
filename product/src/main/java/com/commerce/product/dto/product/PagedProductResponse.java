package com.commerce.product.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/10/22 13:42
 * @package com.commerce.ecommapp.dto.product
 * <p>
 * Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedProductResponse {
    public List<ProductSummary> productSummaries;
    public Integer pageNumber;
    public Integer pageSize;
    public Integer totalPages;
    public Long totalElements;
    public boolean islastPage;
}
