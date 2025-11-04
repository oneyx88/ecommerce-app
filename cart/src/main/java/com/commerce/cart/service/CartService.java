package com.commerce.cart.service;

import com.commerce.cart.dto.CartResponse;
import com.commerce.cart.model.CartItem;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Yixi Wan
 * @date 2025/10/29 16:13
 * @package com.commerce.order.service
 * <p>
 * Description:
 */
public interface CartService {
    String addProductToCart(String keycloakId, Long productId, Integer quantity);

    CartResponse getCartByKeycloakId(String keycloakId);

    @Transactional
    CartItem updateProductQuantityInCart(String keycloakId, Long productId, int delete);

    void deleteProductFromCart(String keycloakId, Long productId);

    void clearCart(String keycloakId);
}
