package com.commerce.cart.controller;

import com.commerce.cart.dto.CartResponse;
import com.commerce.cart.model.CartItem;
import com.commerce.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yixi Wan
 * @date 2025/10/29 16:12
 * @package com.commerce.order.controller
 * <p>
 * Description:
 */
@RestController
@RequestMapping("/api")
class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<String> addProductToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity,
            @RequestHeader("X-User-Id") String keycloakId) {

        return new ResponseEntity<>(cartService.addProductToCart(keycloakId, productId, quantity), HttpStatus.CREATED);
    }

    /** Get User's Cart */
    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartResponse> getUserByKeycloakId(@RequestHeader("X-User-Id") String keycloakId) {
        CartResponse cart = cartService.getCartByKeycloakId(keycloakId);
        return ResponseEntity.ok(cart);
    }

    /** Update Product Quantity */
    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartItem> updateProductQuantity(
            @PathVariable Long productId,
            @PathVariable String operation,
            @RequestHeader("X-User-Id") String keycloakId) {

        CartItem cartItem = cartService.updateProductQuantityInCart(keycloakId, productId,
                operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<CartItem>(cartItem, HttpStatus.OK);
    }

    /** Delete Product from Cart */
    @DeleteMapping("/cart/product/{productId}")
    public ResponseEntity<Void> deleteProductFromCart(
            @PathVariable Long productId,
            @RequestHeader("X-User-Id") String keycloakId) {

        cartService.deleteProductFromCart(keycloakId, productId);
        return ResponseEntity.noContent().build();
    }
}
