package com.erp.services.implemented;

import com.erp.dto.CustomerDueSummaryDTO;
import com.erp.dto.CustomerLedgerResponseDTO;
import com.erp.enities.Customer;
import com.erp.enities.PartyLedgerEntry;
import com.erp.enums.PartyType;
import com.erp.repositories.CustomerRepository;
import com.erp.repositories.PartyLedgerEntryRepository;
import com.erp.services.CustomerLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerLedgerServiceImplement implements CustomerLedgerService {

    private final CustomerRepository customerRepository;
    private final PartyLedgerEntryRepository ledgerRepository;

    @Override
    public List<CustomerDueSummaryDTO> getAllCustomerDueSummary() {
        List<CustomerDueSummaryDTO> result = new ArrayList<>();

        for (Customer customer : customerRepository.findAll()) {
            result.add(buildSummary(customer));
        }

        return result;
    }

    @Override
    public CustomerDueSummaryDTO getCustomerDueSummaryById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found."));
        return buildSummary(customer);
    }

    @Override
    public List<CustomerLedgerResponseDTO> getCustomerLedger(Long customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found."));

        List<PartyLedgerEntry> entries = ledgerRepository.findByPartyTypeAndPartyId(
                PartyType.CUSTOMER,
                customerId,
                Sort.by(Sort.Direction.ASC, "id")
        );

        List<CustomerLedgerResponseDTO> response = new ArrayList<>();
        BigDecimal running = BigDecimal.ZERO;

        for (PartyLedgerEntry entry : entries) {
            BigDecimal debit = entry.getDebitAmount() != null ? entry.getDebitAmount() : BigDecimal.ZERO;
            BigDecimal credit = entry.getCreditAmount() != null ? entry.getCreditAmount() : BigDecimal.ZERO;

            running = running.add(credit).subtract(debit);

            CustomerLedgerResponseDTO dto = new CustomerLedgerResponseDTO();
            dto.setDate(entry.getEntryDate());
            dto.setReferenceId(entry.getReferenceId());
            dto.setReferenceType(entry.getReferenceType());
            dto.setTransactionType(entry.getTransactionType().name());
            dto.setDebit(debit);
            dto.setCredit(credit);
            dto.setRunningBalance(running);
            dto.setRemarks(entry.getRemarks());

            dto.setReferenceNo(resolveReferenceNo(entry));
            response.add(dto);
        }

        return response;
    }

    private CustomerDueSummaryDTO buildSummary(Customer customer) {
        List<PartyLedgerEntry> entries = ledgerRepository.findByPartyTypeAndPartyId(
                PartyType.CUSTOMER,
                customer.getId(),
                Sort.by(Sort.Direction.ASC, "id")
        );

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalPayment = BigDecimal.ZERO;

        for (PartyLedgerEntry entry : entries) {
            BigDecimal debit = entry.getDebitAmount() != null ? entry.getDebitAmount() : BigDecimal.ZERO;
            BigDecimal credit = entry.getCreditAmount() != null ? entry.getCreditAmount() : BigDecimal.ZERO;

            totalSales = totalSales.add(credit);
            totalPayment = totalPayment.add(debit);
        }

        CustomerDueSummaryDTO dto = new CustomerDueSummaryDTO();
        dto.setCustomerId(customer.getId());
        dto.setCustomerName(customer.getName());
        dto.setMobileNumber(customer.getMobileNumber());
        dto.setCompanyName(customer.getCompanyName());
        dto.setTotalSales(totalSales);
        dto.setTotalApprovedPayment(totalPayment);
        dto.setCurrentDue(totalSales.subtract(totalPayment));

        return dto;
    }

    private String resolveReferenceNo(PartyLedgerEntry entry) {
        // placeholder - can be improved with joins if needed
        return entry.getReferenceType() + "-" + entry.getReferenceId();
    }
}
