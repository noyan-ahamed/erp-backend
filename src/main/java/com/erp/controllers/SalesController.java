package com.erp.controllers;

import com.erp.dto.CustomerSearchResponseDTO;
import com.erp.dto.SalesCreateRequestDTO;
import com.erp.dto.SalesResponseDTO;
import com.erp.services.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping
    public SalesResponseDTO createSale(@RequestBody SalesCreateRequestDTO request) {
        return salesService.createSale(request);
    }

    @GetMapping("/{id}")
    public SalesResponseDTO getSaleById(@PathVariable Long id) {
        return salesService.getSaleById(id);
    }

    @GetMapping
    public List<SalesResponseDTO> getAllSales() {
        return salesService.getAllSales();
    }

    @GetMapping("/customer-search")
    public List<CustomerSearchResponseDTO> searchCustomers(@RequestParam String keyword) {
        return salesService.searchCustomers(keyword);
    }
}
