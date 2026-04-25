package com.example.ordermanagement.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name("order-created")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic shipmentStatusTopic() {
        return TopicBuilder.name("shipment-status-updated")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic lowStockTopic() {
        return TopicBuilder.name("low-stock-alert")
                .partitions(1).replicas(1).build();
    }
}