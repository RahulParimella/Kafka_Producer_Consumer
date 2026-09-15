package com.example.kafka.demo.controller;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final AdminClient adminClient;

    public OrderController(AdminClient adminClient) {
        this.adminClient = adminClient;
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
}
