package com.example.ordermanagement.kafka;

import com.example.ordermanagement.dto.StockAdjustmentRequest;
import com.example.ordermanagement.entity.InventoryLog;
import com.example.ordermanagement.event.OrderCreatedEvent;
import com.example.ordermanagement.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryEventConsumer {

    @Autowired
    private InventoryService inventoryService;

    @KafkaListener(topics = "order-created", groupId = "order-management-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        for (var item : event.getItems()) {
            StockAdjustmentRequest req = new StockAdjustmentRequest();
            req.setQuantity(item.getQuantity());
            req.setMovementType(InventoryLog.MovementType.RESERVED);
            req.setReferenceNumber(event.getOrderNumber());
            inventoryService.adjustStock(item.getProductId(), req);
        }
    }
}