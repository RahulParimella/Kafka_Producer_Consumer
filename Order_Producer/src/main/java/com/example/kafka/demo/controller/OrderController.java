package com.example.kafka.demo.controller;

import com.example.kafka.demo.model.Order;
import com.example.kafka.demo.service.OrderProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProducerService orderProducerService;
    private final Map<Long, Order> orders = new HashMap<>();

    public OrderController(OrderProducerService orderProducerService) {
        this.orderProducerService = orderProducerService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Order order) {
        Order publishedOrder = orderProducerService.sendOrder(order);
        orders.put(publishedOrder.getId(), publishedOrder);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order placed successfully and sent to Kafka.");
        response.put("order", publishedOrder);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = orders.get(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
}
