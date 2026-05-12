package com.erp.repositories;

import com.erp.enities.SalesOrderHeader;
import com.erp.enities.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {
    List<SalesOrderItem> findBySalesOrder(SalesOrderHeader salesOrder);
}
