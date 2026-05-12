package com.erp.repositories;

import com.erp.enities.PurchaseOrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    @Query("SELECT poi FROM PurchaseOrderItem poi " +
            "WHERE poi.product.id = :productId " +
            "ORDER BY poi.id DESC")
    List<PurchaseOrderItem> findLatestPurchase(
            @Param("productId") Long productId,
            Pageable pageable
    );
}