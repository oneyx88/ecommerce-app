package com.commerce.order.service;

import com.commerce.order.dto.CartResponse;
import com.commerce.order.model.CartItem;
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
}
