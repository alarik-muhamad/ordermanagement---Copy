package com.example.ordermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ShipmentRequest {

    @NotNull(message = "Order ID wajib diisi")
    private Long orderId;

    @NotBlank(message = "Nama kurir wajib diisi")
    private String courierName;

    private String courierService;

    @NotBlank(message = "Nama penerima wajib diisi")
    private String recipientName;

    @NotBlank(message = "Alamat penerima wajib diisi")
    private String recipientAddress;

    @NotBlank(message = "Nomor telepon penerima wajib diisi")
    private String recipientPhone;

    @Positive(message = "Biaya pengiriman harus positif")
    private Double shippingCost;

    @Positive(message = "Berat harus positif")
    private Double weightKg;

    private String notes;

    // Estimated delivery in days from now
    private Integer estimatedDays;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCourierName() { return courierName; }
    public void setCourierName(String courierName) { this.courierName = courierName; }

    public String getCourierService() { return courierService; }
    public void setCourierService(String courierService) { this.courierService = courierService; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getRecipientAddress() { return recipientAddress; }
    public void setRecipientAddress(String recipientAddress) { this.recipientAddress = recipientAddress; }

    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }

    public Double getShippingCost() { return shippingCost; }
    public void setShippingCost(Double shippingCost) { this.shippingCost = shippingCost; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getEstimatedDays() { return estimatedDays; }
    public void setEstimatedDays(Integer estimatedDays) { this.estimatedDays = estimatedDays; }
}
