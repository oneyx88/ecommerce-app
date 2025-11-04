package com.commerce.order.clients;

import com.commerce.order.dto.CartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:26
 * @package com.commerce.order.service
 * <p>
 * Description:
 */
@Service
public class CartClientService {
    @Autowired
    private CartFeignClient cartFeignClient;

    public CartResponse getCartByKeyCloakId(String keycloakId) {
        return cartFeignClient.getCartByKeycloakId(keycloakId);
    }

    public void clearCart(String keycloakId) {
        cartFeignClient.clearCart(keycloakId);
    }
}
