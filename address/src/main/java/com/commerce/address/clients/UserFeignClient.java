package com.commerce.address.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Yixi Wan
 * @date 2025/11/2 15:27
 * @package com.commerce.order.clients
 * <p>
 * Description:
 */
@FeignClient(name = "user-service", url = "http://localhost:8081/api")
public interface UserFeignClient {

    @GetMapping("/active/{keycloakId}")
    ResponseEntity<Boolean> isUserActive(@PathVariable String keycloakId);
}
