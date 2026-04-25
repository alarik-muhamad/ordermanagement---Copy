package com.example.ordermanagement.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.ordermanagement.entity.Order;
import com.example.ordermanagement.event.OrderCreatedEvent;

@Service
public class OrderEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreated(Order order) {
        OrderCreatedEvent event = OrderCreatedEvent.from(order); // pakai static factory
        kafkaTemplate.send("order-created", String.valueOf(order.getId()), event);
    }
}