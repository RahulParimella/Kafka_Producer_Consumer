package com.example.kafka.demo.controller;

import com.example.kafka.demo.model.Order;
import com.example.kafka.demo.service.OrderProducerService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducerService orderProducerService;

    private final Map<Long, Order> orders = new HashMap<>();

    public OrderController(OrderProducerService orderProducerService) {
        this.orderProducerService = orderProducerService;
    }

    @PostMapping
    public String createOrder(@RequestBody Order order) {

        orders.put(order.getId(), order);

        orderProducerService.sendOrder(order);

        return "Order created successfully";
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {

        return orders.get(id);
    }
}
