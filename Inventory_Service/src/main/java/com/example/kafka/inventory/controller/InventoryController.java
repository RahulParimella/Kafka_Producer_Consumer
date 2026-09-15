package com.example.kafka.inventory.controller;

import com.example.kafka.inventory.model.ProductInventory;
import com.example.kafka.inventory.repository.InventoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @PostMapping("/seed")
    public ResponseEntity<Map<String, Object>> seedInventory() {
        Map<String, Object> response = new HashMap<>();

        inventoryRepository.save(new ProductInventory(null, "Laptop", 5));
        inventoryRepository.save(new ProductInventory(null, "Mobile", 10));
        inventoryRepository.save(new ProductInventory(null, "Headphone", 8));

        response.put("message", "Inventory initialized with sample stock values.");
        response.put("items", inventoryRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productName}")
    public ResponseEntity<Map<String, Object>> getAvailability(@PathVariable String productName) {
        Map<String, Object> response = new HashMap<>();

        ProductInventory inventory = inventoryRepository.findByProductName(productName)
                .orElse(null);

        if (inventory == null) {
            response.put("productName", productName);
            response.put("availableQuantity", 0);
            response.put("message", "No inventory found for this product.");
            return ResponseEntity.ok(response);
        }

        response.put("productName", inventory.getProductName());
        response.put("availableQuantity", inventory.getAvailableQuantity());
        return ResponseEntity.ok(response);
    }
}
