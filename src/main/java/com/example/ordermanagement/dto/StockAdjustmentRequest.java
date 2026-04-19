package com.example.ordermanagement.dto;

import com.example.ordermanagement.entity.InventoryLog;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {

    @NotNull(message = "Jumlah wajib diisi")
    @Min(value = 1, message = "Jumlah minimal 1")
    private Integer quantity;

    @NotNull(message = "Tipe pergerakan wajib diisi")
    private InventoryLog.MovementType movementType;

    private String referenceNumber;
    private String notes;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public InventoryLog.MovementType getMovementType() { return movementType; }
    public void setMovementType(InventoryLog.MovementType movementType) { this.movementType = movementType; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
