package com.commerce.order.clients;

import com.commerce.order.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:20
 * @package com.commerce.order.clients
 * <p>
 * Description:
 */
@FeignClient(name = "cart-service", url = "http://localhost:8083", path = "/api")
public interface CartFeignClient {

    @GetMapping("/carts/users/cart")
    CartResponse getCartByKeycloakId(@RequestHeader("X-User-Id") String keycloakId);

    @DeleteMapping("/cart/clear")
    ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") String keycloakId);
}
