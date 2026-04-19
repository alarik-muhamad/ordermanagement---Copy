package com.example.ordermanagement.controller;

import com.example.ordermanagement.dto.ShipmentRequest;
import com.example.ordermanagement.dto.TrackingUpdateRequest;
import com.example.ordermanagement.entity.Shipment;
import com.example.ordermanagement.entity.ShipmentTracking;
import com.example.ordermanagement.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    // GET /api/shipments
    @GetMapping
    public ResponseEntity<List<Shipment>> getAll() {
        return ResponseEntity.ok(shipmentService.getAllShipments());
    }

    // GET /api/shipments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getById(@PathVariable Long id) {
        return shipmentService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/shipments/tracking/{trackingNumber}
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Shipment> getByTracking(@PathVariable String trackingNumber) {
        return shipmentService.getByTrackingNumber(trackingNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/shipments/order/{orderId}
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Shipment> getByOrder(@PathVariable Long orderId) {
        return shipmentService.getByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/shipments/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Shipment>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(shipmentService.getByStatus(status));
    }

    // GET /api/shipments/{id}/tracking-history
    @GetMapping("/{id}/tracking-history")
    public ResponseEntity<List<ShipmentTracking>> getTrackingHistory(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getTrackingHistory(id));
    }

    // POST /api/shipments — buat shipment baru dari order
    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody @Valid ShipmentRequest request) {
        Shipment created = shipmentService.createShipment(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // PATCH /api/shipments/{id}/tracking — update posisi & status pengiriman
    @PatchMapping("/{id}/tracking")
    public ResponseEntity<Shipment> updateTracking(
            @PathVariable Long id,
            @RequestBody @Valid TrackingUpdateRequest request) {
        Shipment updated = shipmentService.updateTracking(id, request);
        return ResponseEntity.ok(updated);
    }
}
