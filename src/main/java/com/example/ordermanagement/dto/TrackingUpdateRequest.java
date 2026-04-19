package com.example.ordermanagement.dto;

import com.example.ordermanagement.entity.Shipment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TrackingUpdateRequest {

    @NotBlank(message = "Lokasi wajib diisi")
    private String location;

    @NotBlank(message = "Deskripsi wajib diisi")
    private String description;

    @NotNull(message = "Status wajib diisi")
    private Shipment.ShipmentStatus status;

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Shipment.ShipmentStatus getStatus() { return status; }
    public void setStatus(Shipment.ShipmentStatus status) { this.status = status; }
}
