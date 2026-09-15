package com.example.kafka.inventory.service;

import com.example.kafka.inventory.model.OrderEvent;
import com.example.kafka.inventory.model.ProductInventory;
import com.example.kafka.inventory.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    @KafkaListener(topics = "order-topic", groupId = "inventory-service")
    public void consumeOrder(OrderEvent orderEvent) {
        if (orderEvent == null) {
            log.warn("Received null order event");
            return;
        }

        log.info("Inventory check started for orderId={}, product={}, quantity={}",
                orderEvent.getId(), orderEvent.getProduct(), orderEvent.getQuantity());

        if (orderEvent.getQuantity() <= 0) {
            log.warn("Inventory rejected: invalid quantity for orderId={}", orderEvent.getId());
            return;
        }

        ProductInventory inventory = inventoryRepository.findByProductName(orderEvent.getProduct())
                .orElse(null);

        if (inventory == null) {
            log.warn("Inventory not found for product={} orderId={}", orderEvent.getProduct(), orderEvent.getId());
            return;
        }

        if (inventory.getAvailableQuantity() < orderEvent.getQuantity()) {
            log.warn("Insufficient stock for product={}, available={}, requested={}",
                    orderEvent.getProduct(), inventory.getAvailableQuantity(), orderEvent.getQuantity());
            return;
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - orderEvent.getQuantity());
        inventoryRepository.save(inventory);

        log.info("Inventory updated for product={} from {} to {} after orderId={}",
                inventory.getProductName(),
                inventory.getAvailableQuantity() + orderEvent.getQuantity(),
                inventory.getAvailableQuantity(),
                orderEvent.getId());
    }
}
