package com.example.ordermanagement.service;

import com.example.ordermanagement.dto.ShipmentRequest;
import com.example.ordermanagement.dto.TrackingUpdateRequest;
import com.example.ordermanagement.entity.Order;
import com.example.ordermanagement.entity.Shipment;
import com.example.ordermanagement.entity.ShipmentTracking;
import com.example.ordermanagement.repository.OrderRepository;
import com.example.ordermanagement.repository.ShipmentRepository;
import com.example.ordermanagement.repository.ShipmentTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentTrackingRepository trackingRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ─── GET ─────────────────────────────────────────────────────────────────────

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> getById(Long id) {
        return shipmentRepository.findById(id);
    }

    public Optional<Shipment> getByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }

    public Optional<Shipment> getByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }

    public List<Shipment> getByStatus(String status) {
        try {
            Shipment.ShipmentStatus s = Shipment.ShipmentStatus.valueOf(status.toUpperCase());
            return shipmentRepository.findByStatus(s);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Status tidak valid: " + status
                    + ". Pilihan: PREPARING, SHIPPED, IN_TRANSIT, DELIVERED, FAILED, RETURNED");
        }
    }

    public List<ShipmentTracking> getTrackingHistory(Long shipmentId) {
        if (!shipmentRepository.existsById(shipmentId)) {
            throw new RuntimeException("Shipment tidak ditemukan: " + shipmentId);
        }
        return trackingRepository.findByShipmentIdOrderByTimestampDesc(shipmentId);
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────────

    @Transactional
    public Shipment createShipment(ShipmentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan: " + request.getOrderId()));

        // Cek apakah order sudah punya shipment
        if (shipmentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new RuntimeException("Order ini sudah memiliki shipment.");
        }

        // Order harus berstatus CONFIRMED atau PROCESSING
        if (!List.of("CONFIRMED", "PROCESSING", "PENDING").contains(order.getStatus().toUpperCase())) {
            throw new RuntimeException("Order harus berstatus CONFIRMED atau PROCESSING untuk dibuat shipment.");
        }

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(generateTrackingNumber(request.getCourierName()));
        shipment.setOrder(order);
        shipment.setStatus(Shipment.ShipmentStatus.PREPARING);
        shipment.setCourierName(request.getCourierName());
        shipment.setCourierService(request.getCourierService());
        shipment.setRecipientName(request.getRecipientName());
        shipment.setRecipientAddress(request.getRecipientAddress());
        shipment.setRecipientPhone(request.getRecipientPhone());
        shipment.setShippingCost(request.getShippingCost());
        shipment.setWeightKg(request.getWeightKg());
        shipment.setNotes(request.getNotes());
        shipment.setCreatedAt(LocalDateTime.now());

        if (request.getEstimatedDays() != null) {
            shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(request.getEstimatedDays()));
        }

        Shipment saved = shipmentRepository.save(shipment);

        // Log initial tracking
        ShipmentTracking tracking = new ShipmentTracking(
                saved, "Gudang", "Paket sedang dipersiapkan", Shipment.ShipmentStatus.PREPARING);
        trackingRepository.save(tracking);

        // Update order status to PROCESSING
        order.setStatus("PROCESSING");
        orderRepository.save(order);

        return saved;
    }

    // ─── UPDATE TRACKING ─────────────────────────────────────────────────────────

    @Transactional
    public Shipment updateTracking(Long shipmentId, TrackingUpdateRequest request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment tidak ditemukan: " + shipmentId));

        // Validasi transisi status
        validateStatusTransition(shipment.getStatus(), request.getStatus());

        shipment.setStatus(request.getStatus());

        // Set timestamp berdasarkan status
        if (request.getStatus() == Shipment.ShipmentStatus.SHIPPED) {
            shipment.setShippedAt(LocalDateTime.now());
        } else if (request.getStatus() == Shipment.ShipmentStatus.DELIVERED) {
            shipment.setDeliveredAt(LocalDateTime.now());
            // Update order status to COMPLETED
            Order order = shipment.getOrder();
            order.setStatus("COMPLETED");
            orderRepository.save(order);
        } else if (request.getStatus() == Shipment.ShipmentStatus.FAILED
                || request.getStatus() == Shipment.ShipmentStatus.RETURNED) {
            // Update order status back
            Order order = shipment.getOrder();
            order.setStatus("CANCELLED");
            orderRepository.save(order);
        }

        Shipment saved = shipmentRepository.save(shipment);

        // Tambah tracking history
        ShipmentTracking tracking = new ShipmentTracking(
                saved, request.getLocation(), request.getDescription(), request.getStatus());
        trackingRepository.save(tracking);

        return saved;
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────────────────────────

    private String generateTrackingNumber(String courierName) {
        String prefix = courierName.replaceAll("\\s+", "").toUpperCase();
        if (prefix.length() > 3) prefix = prefix.substring(0, 3);
        return prefix + "-" + System.currentTimeMillis();
    }

    private void validateStatusTransition(Shipment.ShipmentStatus current, Shipment.ShipmentStatus next) {
        boolean valid = switch (current) {
            case PREPARING -> next == Shipment.ShipmentStatus.SHIPPED
                    || next == Shipment.ShipmentStatus.FAILED;
            case SHIPPED -> next == Shipment.ShipmentStatus.IN_TRANSIT
                    || next == Shipment.ShipmentStatus.DELIVERED
                    || next == Shipment.ShipmentStatus.FAILED;
            case IN_TRANSIT -> next == Shipment.ShipmentStatus.DELIVERED
                    || next == Shipment.ShipmentStatus.FAILED
                    || next == Shipment.ShipmentStatus.RETURNED;
            case DELIVERED, FAILED, RETURNED -> false; // terminal states
        };

        if (!valid) {
            throw new RuntimeException("Transisi status tidak valid: " + current + " → " + next);
        }
    }
}
