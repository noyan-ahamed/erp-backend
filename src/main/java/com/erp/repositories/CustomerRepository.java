package com.erp.repositories;

import com.erp.enities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByMobileNumber(String mobileNumber);
    List<Customer> findByNameContainingIgnoreCase(String name);
    List<Customer> findByNameContainingIgnoreCaseOrMobileNumberContaining(String name, String mobile);

    List<Customer> findByMobileNumberContainingOrNameContainingIgnoreCase(String mobileKeyword, String nameKeyword);
}
