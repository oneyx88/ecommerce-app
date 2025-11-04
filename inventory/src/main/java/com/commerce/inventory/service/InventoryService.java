package com.commerce.inventory.service;

import com.commerce.inventory.dto.InventoryRequest;
import com.commerce.inventory.dto.InventoryResponse;
import com.commerce.inventory.dto.StockOperationRequest;

import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/11/3 11:12
 * @package com.commerce.inventory.service
 * <p>
 * Description:
 */
public interface InventoryService {
    InventoryResponse getInventoryByProductId(Long productId);

    void lockStock(StockOperationRequest request);

    void confirmStock(Long productId, int quantity);

    void releaseStock(Long productId, int quantity);

    void deleteInventory(Long productId);

    InventoryResponse updateInventory(Long productId, InventoryRequest request);

    InventoryResponse createInventory(Long productId, InventoryRequest request);

    List<InventoryResponse> getAllInventories();
}
