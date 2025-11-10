package com.commerce.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Yixi Wan
 * @date 2025/11/5 20:18
 * @package com.commerce.gateway
 * <p>
 * Description:
 */
/**
 * 熔断降级统一入口
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/users")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("user-service is temporarily unavailable.");
    }

    @RequestMapping("/orders")
    public ResponseEntity<String> orderFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("order-service is temporarily unavailable.");
    }

    @RequestMapping("/products")
    public ResponseEntity<String> productFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("product-service is temporarily unavailable.");
    }

    @RequestMapping("/inventories")
    public ResponseEntity<String> inventoryFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("inventory-service is temporarily unavailable.");
    }

    @RequestMapping("/payments")
    public ResponseEntity<String> paymentFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("payment-service is temporarily unavailable.");
    }

    @RequestMapping("/carts")
    public ResponseEntity<String> cartFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("cart-service is temporarily unavailable.");
    }

    @RequestMapping("/addresses")
    public ResponseEntity<String> addressFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("address-service is temporarily unavailable.");
    }
}
