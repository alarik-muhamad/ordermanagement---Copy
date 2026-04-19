package com.example.ordermanagement.controller;

import com.example.ordermanagement.dto.InventoryRequest;
import com.example.ordermanagement.dto.StockAdjustmentRequest;
import com.example.ordermanagement.entity.Inventory;
import com.example.ordermanagement.entity.InventoryLog;
import com.example.ordermanagement.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // GET /api/inventory — semua inventory
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    // GET /api/inventory/low-stock — produk yang stok hampir habis
    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventory>> getLowStock() {
        return ResponseEntity.ok(inventoryService.getLowStockInventories());
    }

    // GET /api/inventory/product/{productId}
    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getByProduct(@PathVariable Long productId) {
        return inventoryService.getByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/inventory/product/{productId}/logs — riwayat pergerakan stok
    @GetMapping("/product/{productId}/logs")
    public ResponseEntity<List<InventoryLog>> getLogs(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getLogsByProduct(productId));
    }

    // POST /api/inventory — inisialisasi inventory baru untuk produk
    @PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody @Valid InventoryRequest request) {
        Inventory created = inventoryService.createInventory(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // PATCH /api/inventory/product/{productId}/adjust — tambah/kurang/adjust stok
    @PatchMapping("/product/{productId}/adjust")
    public ResponseEntity<Inventory> adjustStock(
            @PathVariable Long productId,
            @RequestBody @Valid StockAdjustmentRequest request) {
        Inventory updated = inventoryService.adjustStock(productId, request);
        return ResponseEntity.ok(updated);
    }

    // PATCH /api/inventory/product/{productId}/reorder-level — update reorder level
    @PatchMapping("/product/{productId}/reorder-level")
    public ResponseEntity<Inventory> updateReorderLevel(
            @PathVariable Long productId,
            @RequestParam Integer level) {
        Inventory updated = inventoryService.updateReorderLevel(productId, level);
        return ResponseEntity.ok(updated);
    }
}
