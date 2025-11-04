package com.commerce.order.clients;

import com.commerce.order.dto.StockOperationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:57
 * @package com.commerce.order.clients
 * <p>
 * Description:
 */
@Service
public class InventoryClientService {
    @Autowired
    private InventoryFeignClient inventoryFeignClient;

    public void lockStock(Long productId, Integer quantity) {
        inventoryFeignClient.lockStock(new StockOperationRequest(productId, quantity));
    }
}
