package com.commerce.cart.controller;

import com.commerce.cart.dto.CartResponse;
import com.commerce.cart.model.CartItem;
import com.commerce.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yixi Wan
 * @date 2025/10/29 16:12
 * @package com.commerce.order.controller
 * <p>
 * Description:
 */
@RestController
@RequestMapping("/api/v1/carts")
class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/products/{productId}/quantity/{quantity}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> addProductToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity,
            @RequestHeader("X-User-Id") String keycloakId) {

        return new ResponseEntity<>(cartService.addProductToCart(keycloakId, productId, quantity), HttpStatus.CREATED);
    }

    /** Get User's Cart */
    @GetMapping("/users/cart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> getUserCart(@RequestHeader("X-User-Id") String keycloakId) {
        CartResponse cart = cartService.getCartByKeycloakId(keycloakId);
        return ResponseEntity.ok(cart);
    }

    /** Update Product Quantity */
    @PutMapping("/products/{productId}/quantity/{operation}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartItem> updateProductQuantity(
            @PathVariable Long productId,
            @PathVariable String operation,
            @RequestHeader("X-User-Id") String keycloakId) {

        CartItem cartItem = cartService.updateProductQuantityInCart(keycloakId, productId,
                operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<CartItem>(cartItem, HttpStatus.OK);
    }

    /** Delete Product from Cart */
    @DeleteMapping("/product/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteProductFromCart(
            @PathVariable Long productId,
            @RequestHeader("X-User-Id") String keycloakId) {

        cartService.deleteProductFromCart(keycloakId, productId);
        return ResponseEntity.noContent().build();
    }

    /** Clear Entire Cart */
    @DeleteMapping("/users/{keycloakId}")
    @PreAuthorize("hasRole('INTERNAL')")
    public ResponseEntity<Void> clearCart(@PathVariable String keycloakId) {

        cartService.clearCart(keycloakId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/users/{keycloakId}/cart")
    @PreAuthorize("hasRole('INTERNAL')")
    public ResponseEntity<CartResponse> getCartBykeycloakId(@PathVariable String keycloakId) {
        CartResponse cart = cartService.getCartByKeycloakId(keycloakId);
        return ResponseEntity.ok(cart);
    }

}
