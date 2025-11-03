package com.commerce.product.service.category;


import com.commerce.product.dto.category.CategoryRequest;
import com.commerce.product.dto.category.CategoryResponse;
import com.commerce.product.dto.category.PagedCategoryResponse;

/**
 * @author Yixi Wan
 * @date 2025/10/21 00:17
 * @package com.commerce.ecommapp.service
 * <p>
 * Description:
 */
public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    PagedCategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest);
    void deleteCategory(Long categoryId);
}
