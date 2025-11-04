package com.commerce.product.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Yixi Wan
 * @date 2025/11/3 14:52
 * @package com.commerce.product.clients
 * <p>
 * Description:
 */
@Service
public class InventoryClientService {
    @Autowired
    private InventoryFeignClient inventoryFeignClient;

    public InventoryResponse getInventoryByProductId(Long productId) {
        return inventoryFeignClient.getInventoryByProductId(productId);
    }
}
