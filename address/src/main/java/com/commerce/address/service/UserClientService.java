package com.commerce.address.service;

import com.commerce.address.clients.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Yixi Wan
 * @date 2025/11/2 17:53
 * @package com.commerce.address.service
 * <p>
 * Description:
 */
@Service
public class UserClientService {
    @Autowired
    private UserFeignClient userFeignClient;

    public boolean isUserActive(String keycloakId) {
        ResponseEntity<Boolean> response = userFeignClient.isUserActive(keycloakId);
        return response.getBody();
    }
}
