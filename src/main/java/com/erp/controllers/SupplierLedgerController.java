package com.erp.controllers;

import com.erp.dto.SupplierDueSummaryDTO;
import com.erp.dto.SupplierLedgerResponseDTO;
import com.erp.services.SupplierLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier-ledger")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class SupplierLedgerController {

    private final SupplierLedgerService supplierLedgerService;

    @GetMapping("/{supplierId}")
    public List<SupplierLedgerResponseDTO> getSupplierLedger(@PathVariable Long supplierId) {
        return supplierLedgerService.getSupplierLedger(supplierId);
    }

    @GetMapping("/due-summary/{supplierId}")
    public SupplierDueSummaryDTO getSupplierDueSummary(@PathVariable Long supplierId) {
        return supplierLedgerService.getSupplierDueSummary(supplierId);
    }


    @GetMapping("/due-summary")
    public List<SupplierDueSummaryDTO> getAllSupplierDueSummaries() {
        return supplierLedgerService.getAllSupplierDueSummaries();
    }
}
