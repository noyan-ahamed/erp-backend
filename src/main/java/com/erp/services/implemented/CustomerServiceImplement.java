package com.erp.services.implemented;

import com.erp.dto.CustomerDTO;
import com.erp.dto.QuickCustomerCreateRequestDTO;
import com.erp.enities.Customer;
import com.erp.repositories.CustomerRepository;
import com.erp.services.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImplement implements CustomerService {

    private final CustomerRepository customerRepo;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    @Override
    @Transactional
    public Customer createCustomer(CustomerDTO dto) {
        Customer customer = new Customer();
        mapDtoToEntity(dto, customer);
        return customerRepo.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        mapDtoToEntity(dto, customer);
        return customerRepo.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepo.deleteById(id);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepo.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }


    //new employee can create customer
    @Override
    public Customer quickCreate(QuickCustomerCreateRequestDTO request) {
        customerRepo.findByMobileNumber(request.getMobileNumber()).ifPresent(c -> {
            throw new RuntimeException("Customer with this mobile already exists.");
        });

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setCompanyName(request.getCompanyName());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setAddress(request.getAddress());

        return customerRepo.save(customer);
    }

    private void mapDtoToEntity(CustomerDTO dto, Customer customer) {
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setCompanyName(dto.getCompanyName());
        customer.setMobileNumber(dto.getMobileNumber());
        customer.setAddress(dto.getAddress());
    }
}
