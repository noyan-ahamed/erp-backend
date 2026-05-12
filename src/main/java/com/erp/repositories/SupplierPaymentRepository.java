package com.erp.repositories;

import com.erp.enities.SupplierPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierPaymentRepository extends JpaRepository<SupplierPayment, Long> {

    List<SupplierPayment> findBySupplierIdOrderByPaymentDateAscIdAsc(Long supplierId);
}