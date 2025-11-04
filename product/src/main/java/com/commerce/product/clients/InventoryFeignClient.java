package com.commerce.product.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Yixi Wan
 * @date 2025/11/3 14:49
 * @package com.commerce.product.clients
 * <p>
 * Description:
 */
@FeignClient(name = "inventory-service", url = "http://localhost:8086", path = "/api")
public interface InventoryFeignClient {
    @GetMapping("/inventory/product/{productId}")
    InventoryResponse getInventoryByProductId(@PathVariable Long productId);
}
