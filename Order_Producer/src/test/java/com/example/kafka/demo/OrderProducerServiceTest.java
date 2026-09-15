package com.example.kafka.demo;

import com.example.kafka.demo.model.Order;
import com.example.kafka.demo.service.OrderProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class OrderProducerServiceTest {

    @Test
    void shouldRejectInvalidOrder() {
        KafkaTemplate<String, Order> kafkaTemplate = mock(KafkaTemplate.class);
        OrderProducerService service = new OrderProducerService(kafkaTemplate);

        Order order = new Order();
        order.setId(1L);
        order.setProduct("Laptop");
        order.setQuantity(0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.sendOrder(order));

        assertEquals("Order quantity must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldSerializeOrderWithJavaTime() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Order order = new Order(101L, "Rahul", "Laptop", 2, 75000.0, "CREATED", LocalDateTime.now());

        byte[] payload = objectMapper.writeValueAsBytes(order);

        assertTrue(payload.length > 0);
    }
}
