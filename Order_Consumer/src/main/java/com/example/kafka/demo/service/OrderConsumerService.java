package com.example.kafka.demo.service;

import com.example.kafka.demo.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderConsumerService {
    @KafkaListener(
            topics = "order-topic",
            groupId = "order-service"
    )
    public void consume(Order order) {
        log.info( "Received order from Kafka. orderId={}, product={}, quantity={}",
                order.getId(), order.getProduct(), order.getQuantity() );
        // Business logic processOrder(order); log.info( "Order processed successfully. orderId={}", order.getId() ); } private void processOrder(Order order) { log.debug( "Processing order. orderId={}", order.getId() ); // Your actual order processing logic
         }
}
