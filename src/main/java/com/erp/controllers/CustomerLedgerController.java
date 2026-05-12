package com.erp.controllers;

import com.erp.dto.CustomerDueSummaryDTO;
import com.erp.dto.CustomerLedgerResponseDTO;
import com.erp.services.CustomerLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-ledger")
@RequiredArgsConstructor
public class CustomerLedgerController {

    private final CustomerLedgerService customerLedgerService;

    @GetMapping("/due-summary")
    public List<CustomerDueSummaryDTO> getAllDueSummary() {
        return customerLedgerService.getAllCustomerDueSummary();
    }

    @GetMapping("/due-summary/{customerId}")
    public CustomerDueSummaryDTO getDueSummaryById(@PathVariable Long customerId) {
        return customerLedgerService.getCustomerDueSummaryById(customerId);
    }

    @GetMapping("/{customerId}")
    public List<CustomerLedgerResponseDTO> getLedger(@PathVariable Long customerId) {
        return customerLedgerService.getCustomerLedger(customerId);
    }
}
