package com.example.ordermanagement.event;

import java.util.List;
import java.util.stream.Collectors;

import com.example.ordermanagement.entity.Order;

public class OrderCreatedEvent {

    private Long orderId;
    private Long customerId;
    private String orderNumber;
    private List<OrderItemDto> items;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(Long orderId, Long customerId, String orderNumber, List<OrderItemDto> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderNumber = orderNumber;
        this.items = items;
    }

    // Constructor langsung dari entity Order biar gampang
    public static OrderCreatedEvent from(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProduct().getId(),
                        item.getQuantity(),
                        item.getPrice()))
                .collect(Collectors.toList());

        return new OrderCreatedEvent(
                order.getId(),
                order.getCustomer().getId(),
                order.getOrderNumber(),
                itemDtos);
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}