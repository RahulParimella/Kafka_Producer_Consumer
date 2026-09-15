package com.example.kafka.inventory.repository;

import com.example.kafka.inventory.model.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<ProductInventory, Long> {
    Optional<ProductInventory> findByProductName(String productName);
}
