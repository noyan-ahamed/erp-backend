package com.erp.services;

import com.erp.dto.CustomerDTO;
import com.erp.dto.QuickCustomerCreateRequestDTO;
import com.erp.enities.Customer;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Customer createCustomer(CustomerDTO dto);
    Customer updateCustomer(Long id, CustomerDTO dto);
    void deleteCustomer(Long id);
    Customer getCustomerById(Long id);

    //new
    Customer quickCreate(@RequestBody QuickCustomerCreateRequestDTO request);
}
