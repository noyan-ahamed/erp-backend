package com.erp.repositories;

import com.erp.enities.InventoryBatch;
import com.erp.enities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryBatchRepository
        extends JpaRepository<InventoryBatch, Long> {

    List<InventoryBatch>
    findByProductAndRemainingQuantityGreaterThanOrderByReceivedDateAscIdAsc(
            Product product,
            Integer quantity
    );
}