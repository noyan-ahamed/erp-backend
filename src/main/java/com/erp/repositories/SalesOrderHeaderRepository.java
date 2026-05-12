package com.erp.repositories;

import com.erp.enities.Customer;
import com.erp.enities.Employee;
import com.erp.enities.SalesOrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalesOrderHeaderRepository extends JpaRepository<SalesOrderHeader, Long> {
    Optional<SalesOrderHeader> findByInvoiceNumber(String invoiceNumber);
    List<SalesOrderHeader> findByCustomer(Customer customer);
    List<SalesOrderHeader> findBySellerEmployee(Employee employee);
    Optional<SalesOrderHeader> findTopByCustomerOrderBySalesDateDescIdDesc(Customer customer);
}
