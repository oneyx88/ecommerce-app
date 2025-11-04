package com.commerce.inventory.service;

import com.commerce.inventory.kafka.InventoryEvent;
import com.commerce.inventory.dto.InventoryRequest;
import com.commerce.inventory.dto.InventoryResponse;
import com.commerce.inventory.dto.StockOperationRequest;
import com.commerce.inventory.exceptions.ApiException;
import com.commerce.inventory.exceptions.ResourceNotFoundException;
import com.commerce.inventory.model.Inventory;
import com.commerce.inventory.repository.InventoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yixi Wan
 * @date 2025/11/3 11:12
 * @package com.commerce.inventory.service
 * <p>
 * Description:
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    /** 查询所有库存 */
    @Override
    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(inventory -> {
                    InventoryResponse dto = modelMapper.map(inventory, InventoryResponse.class);
                    dto.setLastUpdated(inventory.getUpdatedAt().toString());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /** 查询单个库存 */
    @Override
    public InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "ProductId", productId));
        InventoryResponse dto = modelMapper.map(inventory, InventoryResponse.class);
        dto.setLastUpdated(inventory.getUpdatedAt().toString());
        return dto;
    }

    /** 新增库存 */
    @Override
    public InventoryResponse createInventory(Long productId, InventoryRequest inventoryRequest) {
        Inventory inventory = modelMapper.map(inventoryRequest, Inventory.class);
        inventory.setProductId(productId);
        Inventory saved = inventoryRepository.save(inventory);

//        publishEvent(productId, "CREATE");

        InventoryResponse response = modelMapper.map(saved, InventoryResponse.class);
        response.setLastUpdated(saved.getUpdatedAt().toString());
        return response;
    }

    /** 修改库存 */
    @Override
    @Transactional
    public InventoryResponse updateInventory(Long productId, InventoryRequest inventoryRequest) {
        Inventory existing = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "ProductId", productId));

        existing.setAvailableStock(inventoryRequest.getAvailableStock());
        existing.setLockedStock(inventoryRequest.getLockedStock());
        existing.setSoldStock(inventoryRequest.getSoldStock());

        Inventory updated = inventoryRepository.save(existing);

        // ✅ 不再携带库存数值，只发 productId + eventType
        publishEvent(productId, "UPDATE");

        InventoryResponse response = modelMapper.map(updated, InventoryResponse.class);
        response.setLastUpdated(updated.getUpdatedAt().toString());
        return response;
    }

    /** 删除库存 */
    @Override
    @Transactional
    public void deleteInventory(Long productId) {
        Inventory existing = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "ProductId", productId));
        inventoryRepository.delete(existing);

        publishEvent(productId, "DELETE");
    }

    /** 锁库存 */
    @Override
    @Transactional
    public void lockStock(StockOperationRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "ProductId", request.getProductId()));

        if (inventory.getAvailableStock() < request.getQuantity()) {
            throw new ApiException("Insufficient stock for productId: " + request.getProductId(), HttpStatus.BAD_REQUEST);
        }

        int result = inventoryRepository.lockStock(request.getProductId(), request.getQuantity(), inventory.getVersion());
        if (result == 0) {
            throw new ApiException("Failed to lock stock (possible concurrent modification)", HttpStatus.CONFLICT);
        }

        publishEvent(request.getProductId(), "LOCK");
    }

    /** 确认库存 */
    @Override
    @Transactional
    public void confirmStock(Long productId, int quantity) {
        int result = inventoryRepository.confirmStock(productId, quantity);
        if (result == 0) {
            throw new ApiException("Confirm stock failed for productId: " + productId, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        publishEvent(productId, "CONFIRM");
    }

    /** 释放库存 */
    @Override
    @Transactional
    public void releaseStock(Long productId, int quantity) {
        int result = inventoryRepository.releaseStock(productId, quantity);
        if (result == 0) {
            throw new ApiException("Release stock failed for productId: " + productId, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        publishEvent(productId, "RELEASE");
    }

    /** ✅ 事务提交后异步触发 Kafka 消息 */
    private void publishEvent(Long productId, String eventType) {
        InventoryEvent event = new InventoryEvent(productId, eventType, LocalDateTime.now());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaTemplate.send("inventory-stock-updated", event);
            }
        });
    }

}
