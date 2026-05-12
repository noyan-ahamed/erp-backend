package com.erp.services;

import com.erp.dto.CustomerSearchResponseDTO;
import com.erp.dto.SalesCreateRequestDTO;
import com.erp.dto.SalesResponseDTO;

import java.util.List;

public interface SalesService {
    SalesResponseDTO createSale(SalesCreateRequestDTO request);
    SalesResponseDTO getSaleById(Long id);
    List<SalesResponseDTO> getAllSales();
    List<CustomerSearchResponseDTO> searchCustomers(String keyword);
}
