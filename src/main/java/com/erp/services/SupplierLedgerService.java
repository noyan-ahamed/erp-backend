package com.erp.services;

import com.erp.dto.SupplierDueSummaryDTO;
import com.erp.dto.SupplierLedgerResponseDTO;

import java.util.List;

public interface SupplierLedgerService {

    List<SupplierLedgerResponseDTO> getSupplierLedger(Long supplierId);

    SupplierDueSummaryDTO getSupplierDueSummary(Long supplierId);
    List<SupplierDueSummaryDTO> getAllSupplierDueSummaries();

}