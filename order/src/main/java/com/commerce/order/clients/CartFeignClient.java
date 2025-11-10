package com.commerce.order.clients;

import com.commerce.order.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:20
 * @package com.commerce.order.clients
 * <p>
 * Description:
 */
@FeignClient(name = "cart-service", path = "/api/v1/carts")
public interface CartFeignClient {

    @GetMapping("/users/{keycloakId}/cart")
    CartResponse getCartByKeycloakId(@PathVariable String keycloakId);

    @DeleteMapping("/users/{keycloakId}")
    ResponseEntity<Void> clearCart(@PathVariable String keycloakId);
}