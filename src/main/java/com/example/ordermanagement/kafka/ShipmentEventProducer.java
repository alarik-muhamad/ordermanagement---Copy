package com.example.ordermanagement.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.ordermanagement.dto.TrackingUpdateRequest;
import com.example.ordermanagement.entity.Shipment;
import com.example.ordermanagement.event.ShipmentStatusEvent;

@Service
public class ShipmentEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendStatusUpdate(Shipment shipment, TrackingUpdateRequest request) {
        ShipmentStatusEvent event = new ShipmentStatusEvent(
            shipment.getId(),
            shipment.getOrder().getId(),
            request.getStatus().name(),
            request.getLocation()
        );
        kafkaTemplate.send("shipment-status-updated", String.valueOf(shipment.getId()), event);
    }
}