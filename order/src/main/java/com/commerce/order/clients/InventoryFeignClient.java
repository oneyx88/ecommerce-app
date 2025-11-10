package com.commerce.order.clients;

import com.commerce.order.dto.StockOperationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:53
 * @package com.commerce.order.clients
 * <p>
 * Description:
 */
@FeignClient(name = "inventory-service", path = "/api/v1/inventories")
public interface InventoryFeignClient {
    @PostMapping("/lock")
    ResponseEntity<String> lockStock(@RequestBody StockOperationRequest request);
}