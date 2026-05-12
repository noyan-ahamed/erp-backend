package com.erp.repositories;

import com.erp.enities.PurchaseOrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderHeaderRepository extends JpaRepository<PurchaseOrderHeader, Long> {
}
