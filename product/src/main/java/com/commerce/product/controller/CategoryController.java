package com.commerce.product.controller;


import com.commerce.product.config.AppConstants;
import com.commerce.product.dto.category.CategoryRequest;
import com.commerce.product.dto.category.CategoryResponse;
import com.commerce.product.dto.category.PagedCategoryResponse;
import com.commerce.product.service.category.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // ------------------------------------------------------------
    // 1️⃣ Create Category — Only ADMIN
    // ------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest) {

        CategoryResponse created = categoryService.createCategory(categoryRequest);
        return ResponseEntity
                .created(URI.create("/api/v1/categories/" + created.getId()))
                .body(created);
    }

    // ------------------------------------------------------------
    // 2️⃣ Get All Categories — Everyone can read
    // ------------------------------------------------------------
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<PagedCategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {

        return ResponseEntity.ok(
                categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder)
        );
    }

    // ------------------------------------------------------------
    // 3️⃣ Update Category — Only ADMIN
    // ------------------------------------------------------------
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest categoryRequest) {

        return ResponseEntity.ok(
                categoryService.updateCategory(categoryId, categoryRequest)
        );
    }

    // ------------------------------------------------------------
    // 4️⃣ Delete Category — Only ADMIN
    // ------------------------------------------------------------
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}