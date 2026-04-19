package com.example.ordermanagement.service;

import com.example.ordermanagement.dto.InventoryRequest;
import com.example.ordermanagement.dto.StockAdjustmentRequest;
import com.example.ordermanagement.entity.Inventory;
import com.example.ordermanagement.entity.InventoryLog;
import com.example.ordermanagement.entity.Product;
import com.example.ordermanagement.repository.InventoryLogRepository;
import com.example.ordermanagement.repository.InventoryRepository;
import com.example.ordermanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryLogRepository inventoryLogRepository;

    @Autowired
    private ProductRepository productRepository;

    // ─── GET ALL ────────────────────────────────────────────────────────────────

    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public List<Inventory> getLowStockInventories() {
        return inventoryRepository.findLowStockInventories();
    }

    public List<InventoryLog> getLogsByProduct(Long productId) {
        return inventoryLogRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    // ─── CREATE ─────────────────────────────────────────────────────────────────

    @Transactional
    public Inventory createInventory(InventoryRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product tidak ditemukan: " + request.getProductId()));

        if (inventoryRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new RuntimeException("Inventory untuk produk ini sudah ada. Gunakan endpoint update.");
        }

        Inventory inventory = new Inventory(product, request.getQuantityOnHand(), request.getReorderLevel());
        Inventory saved = inventoryRepository.save(inventory);

        // Log initial stock
        logMovement(product, InventoryLog.MovementType.STOCK_IN,
                request.getQuantityOnHand(), "INIT", "Inisialisasi stok awal");

        return saved;
    }

    // ─── ADJUST STOCK ────────────────────────────────────────────────────────────

    @Transactional
    public Inventory adjustStock(Long productId, StockAdjustmentRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory tidak ditemukan untuk produk: " + productId));

        switch (request.getMovementType()) {
            case STOCK_IN:
                inventory.setQuantityOnHand(inventory.getQuantityOnHand() + request.getQuantity());
                break;

            case STOCK_OUT:
                if (inventory.getQuantityAvailable() < request.getQuantity()) {
                    throw new RuntimeException("Stok tersedia tidak cukup. Tersedia: "
                            + inventory.getQuantityAvailable());
                }
                inventory.setQuantityOnHand(inventory.getQuantityOnHand() - request.getQuantity());
                break;

            case ADJUSTMENT:
                // Direct set: request.quantity = new total onHand
                if (request.getQuantity() < inventory.getQuantityReserved()) {
                    throw new RuntimeException("Jumlah adjustment tidak boleh kurang dari stok reserved: "
                            + inventory.getQuantityReserved());
                }
                inventory.setQuantityOnHand(request.getQuantity());
                break;

            case RESERVED:
                if (inventory.getQuantityAvailable() < request.getQuantity()) {
                    throw new RuntimeException("Stok tersedia tidak cukup untuk direservasi.");
                }
                inventory.setQuantityReserved(inventory.getQuantityReserved() + request.getQuantity());
                break;

            case RELEASED:
                if (inventory.getQuantityReserved() < request.getQuantity()) {
                    throw new RuntimeException("Jumlah release melebihi stok yang direservasi.");
                }
                inventory.setQuantityReserved(inventory.getQuantityReserved() - request.getQuantity());
                break;
        }

        inventory.setLastUpdated(LocalDateTime.now());
        Inventory saved = inventoryRepository.save(inventory);

        logMovement(inventory.getProduct(), request.getMovementType(),
                request.getQuantity(), request.getReferenceNumber(), request.getNotes());

        return saved;
    }

    // ─── UPDATE REORDER LEVEL ────────────────────────────────────────────────────

    @Transactional
    public Inventory updateReorderLevel(Long productId, Integer reorderLevel) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory tidak ditemukan untuk produk: " + productId));
        inventory.setReorderLevel(reorderLevel);
        inventory.setLastUpdated(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────────────────────────

    private void logMovement(Product product, InventoryLog.MovementType type,
                              Integer quantity, String ref, String notes) {
        InventoryLog log = new InventoryLog(product, type, quantity, ref, notes);
        inventoryLogRepository.save(log);
    }
}
