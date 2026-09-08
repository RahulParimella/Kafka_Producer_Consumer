package com.example.kafka.demo.service;

import com.example.kafka.demo.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderProducerService {

    private static final String TOPIC = "order-topic";

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderProducerService(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(Order order) {
        log.info("Sending order to Kafka. orderId={}", order.getId());
        kafkaTemplate.send(TOPIC, order.getId().toString(), order)
                .whenComplete((result, exception) ->
                {
                    if (exception == null) {
                        log.info("Order sent successfully. orderId={}, topic={}, partition={}, offset={}", order.getId(), result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send order to Kafka. orderId={}", order.getId(), exception);
                    }
                });
    }
}
