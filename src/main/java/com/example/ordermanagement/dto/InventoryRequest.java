package com.example.ordermanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryRequest {

    @NotNull(message = "Product ID wajib diisi")
    private Long productId;

    @NotNull(message = "Jumlah stok wajib diisi")
    @Min(value = 0, message = "Stok tidak boleh negatif")
    private Integer quantityOnHand;

    @NotNull(message = "Reorder level wajib diisi")
    @Min(value = 0, message = "Reorder level tidak boleh negatif")
    private Integer reorderLevel;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(Integer quantityOnHand) { this.quantityOnHand = quantityOnHand; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
}
