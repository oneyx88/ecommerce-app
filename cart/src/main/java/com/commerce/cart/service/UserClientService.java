package com.commerce.cart.service;

import com.commerce.cart.clients.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Yixi Wan
 * @date 2025/11/2 15:49
 * @package com.commerce.order.service
 * <p>
 * Description:
 */
@Service
class UserClientService {
    @Autowired
    private UserFeignClient userFeignClient;

    @Cacheable(value = "userActiveCache", key = "#keycloakId")
    public boolean isUserActive(String keycloakId) {
        ResponseEntity<Boolean> response = userFeignClient.isUserActive(keycloakId);
        return response.getBody();
    }

}
