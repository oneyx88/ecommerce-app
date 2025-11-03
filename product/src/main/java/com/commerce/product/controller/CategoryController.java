package com.commerce.product.controller;


import com.commerce.product.config.AppConstants;
import com.commerce.product.dto.category.CategoryRequest;
import com.commerce.product.dto.category.CategoryResponse;
import com.commerce.product.dto.category.PagedCategoryResponse;
import com.commerce.product.service.category.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * @author Yixi Wan
 * @date 2025/10/20 23:13
 * @package com.commerce.ecommapp.controller
 * <p>
 * Description:
 */
@RestController
@RequestMapping("/api")
class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.created(URI.create("/api/admin/category"))
                .body(categoryService.createCategory(categoryRequest));
    }

    @GetMapping("/public/categories")
    public ResponseEntity<PagedCategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder));
    }

    @PutMapping("/admin/category/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryRequest));
    }

    @DeleteMapping("/admin/category/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
