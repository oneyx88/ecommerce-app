package com.commerce.product.controller;


import com.commerce.product.config.AppConstants;
import com.commerce.product.dto.product.PagedProductResponse;
import com.commerce.product.dto.product.ProductRequest;
import com.commerce.product.dto.product.ProductResponse;
import com.commerce.product.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api")
class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductResponse> addProduct(@PathVariable Long categoryId, @RequestBody ProductRequest productRequest) {
        return ResponseEntity.created(URI.create("/api/admin/categories/" + categoryId + "/product"))
                .body(productService.addProduct(categoryId, productRequest));
    }

    @GetMapping("/public/products")
    public ResponseEntity<PagedProductResponse> getAllProducts(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                               @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                               @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY) String sortBy,
                                                               @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        return ResponseEntity.ok(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<PagedProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                                      @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                                      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                                      @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY) String sortBy,
                                                                      @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        return ResponseEntity.ok(productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<PagedProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                     @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                                     @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                                     @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY) String sortBy,
                                                                     @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        return ResponseEntity.ok(productService.searchByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder));
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(productId, productRequest));
    }

    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("product/{productId}/image")
    public ResponseEntity<ProductResponse> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(productService.updateProductImage(productId, image));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

}
