package com.example.kafka.demo.service;

import com.example.kafka.demo.model.Order;
import com.example.kafka.demo.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class OrderConsumerService {

    private final OrderRepository orderRepository;

    public OrderConsumerService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "order-topic", groupId = "order-service")
    public void consume(Order order) {
        if (order == null) {
            log.warn("Received a null order message from Kafka.");
            return;
        }

        log.info("Received order from Kafka. orderId={}, customer={}, product={}, quantity={}",
                order.getId(), order.getCustomerName(), order.getProduct(), order.getQuantity());

        processOrder(order);
    }

    private void processOrder(Order order) {
        try {
            order.setStatus("PROCESSING");
            order.setCreatedAt(order.getCreatedAt() == null ? LocalDateTime.now() : order.getCreatedAt());
            orderRepository.save(order);

            log.info("Order is being processed. orderId={}", order.getId());

            order.setStatus("COMPLETED");
            orderRepository.save(order);

            log.info("Order processed successfully. orderId={}, status={}, totalAmount={} ",
                    order.getId(), order.getStatus(), order.getPrice() * order.getQuantity());
        } catch (Exception exception) {
            log.error("Order processing failed. orderId={}", order.getId(), exception);
        }
    }
}
