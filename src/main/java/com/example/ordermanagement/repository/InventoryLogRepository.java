package com.example.ordermanagement.repository;

import com.example.ordermanagement.entity.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    List<InventoryLog> findByProductIdOrderByCreatedAtDesc(Long productId);

    List<InventoryLog> findByReferenceNumber(String referenceNumber);
}
