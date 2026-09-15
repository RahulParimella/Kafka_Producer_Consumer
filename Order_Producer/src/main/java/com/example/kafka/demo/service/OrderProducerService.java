package com.example.kafka.demo.service;

import com.example.kafka.demo.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderProducerService {

    private static final String TOPIC = "order-topic";

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderProducerService(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Order sendOrder(Order order) {
        validateOrder(order);

        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());

        log.info("Publishing new order to Kafka. orderId={}, customer={}, product={}, quantity={}",
                order.getId(), order.getCustomerName(), order.getProduct(), order.getQuantity());

        try {
            kafkaTemplate.send(TOPIC, order.getId().toString(), order)
                    .get(10, TimeUnit.SECONDS);
            log.info("Order published successfully. orderId={}", order.getId());
            return order;
        } catch (Exception exception) {
            log.error("Failed to publish order. orderId={}", order.getId(), exception);
            throw new IllegalStateException("Order could not be published to Kafka.", exception);
        }
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }
        if (order.getId() == null) {
            throw new IllegalArgumentException("Order id is required.");
        }
        if (order.getCustomerName() == null || order.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (order.getProduct() == null || order.getProduct().isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("Order quantity must be greater than zero.");
        }
        if (order.getPrice() <= 0) {
            throw new IllegalArgumentException("Order price must be greater than zero.");
        }
    }
}
