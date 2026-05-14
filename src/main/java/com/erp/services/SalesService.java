package com.erp.services;

import com.erp.dto.CustomerSearchResponseDTO;
import com.erp.dto.SalesCreateRequestDTO;
import com.erp.dto.SalesResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SalesService {
    SalesResponseDTO createSale(SalesCreateRequestDTO request);
    SalesResponseDTO getSaleById(Long id);
    List<SalesResponseDTO> getAllSales();
    List<CustomerSearchResponseDTO> searchCustomers(String keyword);

    List<SalesResponseDTO> getMySales(
            LocalDate date
    );

    BigDecimal getMyMonthlySales(
            int year,
            int month
    );
}
