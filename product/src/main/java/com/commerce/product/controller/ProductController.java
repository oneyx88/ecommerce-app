package com.commerce.product.controller;


import com.commerce.product.config.AppConstants;
import com.commerce.product.dto.product.PagedProductResponse;
import com.commerce.product.dto.product.ProductRequest;
import com.commerce.product.dto.product.ProductResponse;
import com.commerce.product.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

/**
 * @author Yixi Wan
 * @date 2025/10/22 12:12
 * @package com.commerce.ecommapp.controller
 * <p>
 * Description:
 */
@RestController
@RequestMapping("/api/v1/products")
class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> addProduct(@PathVariable Long categoryId, @RequestBody ProductRequest productRequest) {
        return ResponseEntity.created(URI.create("/api/v1/products/categories/" + categoryId))
                .body(productService.addProduct(categoryId, productRequest));
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<PagedProductResponse> getAllProducts(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                               @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                               @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY) String sortBy,
                                                               @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        return ResponseEntity.ok(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/categories/{categoryId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PagedProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                                      @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                                      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                                      @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY) String sortBy,
                                                                      @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        return ResponseEntity.ok(productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/keyword/{keyword}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PagedProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                     @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                                     @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                                     @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY) String sortBy,
                                                                     @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        return ResponseEntity.ok(productService.searchByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(productId, productRequest));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}/image")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(productService.updateProductImage(productId, image));
    }

    @GetMapping("/{productId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

}
