package com.example.kafka.demo.controller;

import com.example.kafka.demo.model.Order;
import com.example.kafka.demo.repository.OrderRepository;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final AdminClient adminClient;
    private final OrderRepository orderRepository;

    public OrderController(AdminClient adminClient, OrderRepository orderRepository) {
        this.adminClient = adminClient;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/topics")
    public List<String> getAllTopics() throws Exception {
        return adminClient
                .listTopics()
                .names()
                .get()
                .stream()
                .sorted()
                .toList();
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
