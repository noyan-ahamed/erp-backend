package com.erp.services;

import com.erp.dto.CustomerDueSummaryDTO;
import com.erp.dto.CustomerLedgerResponseDTO;

import java.util.List;

public interface CustomerLedgerService {
    List<CustomerDueSummaryDTO> getAllCustomerDueSummary();
    CustomerDueSummaryDTO getCustomerDueSummaryById(Long customerId);
    List<CustomerLedgerResponseDTO> getCustomerLedger(Long customerId);
}
