package com.commerce.inventory.controller;

import com.commerce.inventory.dto.InventoryRequest;
import com.commerce.inventory.dto.InventoryResponse;
import com.commerce.inventory.dto.StockOperationRequest;
import com.commerce.inventory.model.Inventory;
import com.commerce.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/11/3 11:11
 * @package com.commerce.inventory.controller
 * <p>
 * Description:
 */
@RestController
@RequestMapping("/api/v1/inventories")
class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    // 查询库存
    @GetMapping("/product/{productId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    // 新增库存
    @PostMapping("/product/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<InventoryResponse> createInventory(@PathVariable Long productId,
                                                             @RequestBody InventoryRequest request) {
        return ResponseEntity.created(URI.create("/api/v1/inventory/product/"+productId)).body(inventoryService.createInventory(productId, request));
    }

    // 修改库存
    @PutMapping("/product/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<InventoryResponse> updateInventory(@PathVariable Long productId,
                                                             @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventory(productId, request));
    }

    // 删除库存
    @DeleteMapping("/product/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> deleteInventory(@PathVariable Long productId) {
        inventoryService.deleteInventory(productId);
        return ResponseEntity.noContent().build();
    }

    // 锁库存
    @PostMapping("/lock")
    @PreAuthorize("hasRole('INTERNAL')")
    public ResponseEntity<String> lockStock(@RequestBody StockOperationRequest request) {
        inventoryService.lockStock(request);
        return ResponseEntity.ok("Stock locked successfully");
    }

    @PostMapping("/confirm")
    @PreAuthorize("hasRole('INTERNAL')")
    public ResponseEntity<String> confirmStock(@RequestParam Long productId, @RequestParam int quantity) {
        inventoryService.confirmStock(productId, quantity);
        return ResponseEntity.ok("Stock confirmed successfully");
    }

    @PostMapping("/release")
    @PreAuthorize("hasRole('INTERNAL')")
    public ResponseEntity<String> releaseStock(@RequestParam Long productId, @RequestParam int quantity) {
        inventoryService.releaseStock(productId, quantity);
        return ResponseEntity.ok("Stock released successfully");
    }
}

