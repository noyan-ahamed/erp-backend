package com.erp.repositories;

import com.erp.enities.Customer;
import com.erp.enities.CustomerPayment;
import com.erp.enums.CustomerPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {
    List<CustomerPayment> findByCustomer(Customer customer);
    List<CustomerPayment> findByStatus(CustomerPaymentStatus status);
    List<CustomerPayment> findByCustomerAndStatus(Customer customer, CustomerPaymentStatus status);


    Optional<CustomerPayment> findTopByCustomerAndStatusOrderByPaymentDateDescIdDesc(
            Customer customer,
            CustomerPaymentStatus status
    );

    // NEW
    List<CustomerPayment> findByVoucherNoContainingIgnoreCaseOrderByIdDesc(String voucherNo);

    List<CustomerPayment> findByCustomer_MobileNumberContainingIgnoreCaseOrderByIdDesc(String mobileNumber);

    List<CustomerPayment> findByCustomer_NameContainingIgnoreCaseOrderByIdDesc(String name);

    List<CustomerPayment> findAllByOrderByIdDesc();

    List<CustomerPayment> findByStatusOrderByIdDesc(CustomerPaymentStatus status);

    List<CustomerPayment> findByStatusAndVoucherNoContainingIgnoreCaseOrderByIdDesc(
            CustomerPaymentStatus status,
            String voucherNo
    );

    List<CustomerPayment> findByStatusAndCustomer_MobileNumberContainingIgnoreCaseOrderByIdDesc(
            CustomerPaymentStatus status,
            String mobile
    );

    List<CustomerPayment> findByStatusAndCustomer_NameContainingIgnoreCaseOrderByIdDesc(
            CustomerPaymentStatus status,
            String name
    );
}
