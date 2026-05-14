package com.erp.controllers;

import com.erp.dto.CustomerSearchResponseDTO;
import com.erp.dto.SalesCreateRequestDTO;
import com.erp.dto.SalesResponseDTO;
import com.erp.services.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sales")
@PreAuthorize("hasAuthority('EMPLOYEE')")
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

    @GetMapping("/my-sales")
    public List<SalesResponseDTO> getMySales(
            @RequestParam(required = false)
            String date
    ) {

        LocalDate targetDate =
                date != null
                        ? LocalDate.parse(date)
                        : LocalDate.now();

        return salesService.getMySales(
                targetDate
        );
    }

    @GetMapping("/my-monthly-total")
    public BigDecimal getMyMonthlyTotal(
            @RequestParam int year,
            @RequestParam int month
    ) {

        return salesService
                .getMyMonthlySales(
                        year,
                        month
                );
    }

    @GetMapping("/customer-search")
    public List<CustomerSearchResponseDTO> searchCustomers(@RequestParam String keyword) {
        return salesService.searchCustomers(keyword);
    }
}
