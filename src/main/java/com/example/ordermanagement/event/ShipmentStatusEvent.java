package com.example.ordermanagement.event;

public class ShipmentStatusEvent {

    private Long shipmentId;
    private Long orderId;
    private String status;
    private String location;

    public ShipmentStatusEvent() {}

    public ShipmentStatusEvent(Long shipmentId, Long orderId, String status, String location) {
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.status = status;
        this.location = location;
    }

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}