package com.erp.services.implemented;

import com.erp.dto.SupplierDueSummaryDTO;
import com.erp.dto.SupplierLedgerResponseDTO;
import com.erp.enities.PartyLedgerEntry;
import com.erp.enities.PurchaseOrderHeader;
import com.erp.enities.Supplier;
import com.erp.enities.SupplierPayment;
import com.erp.enums.LedgerTransactionType;
import com.erp.enums.PartyType;
import com.erp.repositories.PartyLedgerEntryRepository;
import com.erp.repositories.PurchaseOrderHeaderRepository;
import com.erp.repositories.SupplierPaymentRepository;
import com.erp.repositories.SupplierRepository;
import com.erp.services.SupplierLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierLedgerServiceImplement implements SupplierLedgerService {

    private final PartyLedgerEntryRepository ledgerRepo;
    private final SupplierRepository supplierRepo;
    private final PurchaseOrderHeaderRepository purchaseRepo;
    private final SupplierPaymentRepository paymentRepo;

    @Override
    public List<SupplierLedgerResponseDTO> getSupplierLedger(Long supplierId) {
        List<PartyLedgerEntry> entries =
                ledgerRepo.findByPartyTypeAndPartyIdOrderByEntryDateAscIdAsc(PartyType.SUPPLIER, supplierId);

        List<SupplierLedgerResponseDTO> responseList = new ArrayList<>();
        BigDecimal runningBalance = BigDecimal.ZERO;

        for (PartyLedgerEntry entry : entries) {
            runningBalance = runningBalance.add(entry.getCreditAmount()).subtract(entry.getDebitAmount());

            SupplierLedgerResponseDTO dto = new SupplierLedgerResponseDTO();
            dto.setDate(entry.getEntryDate());
            dto.setTransactionType(entry.getTransactionType());
            dto.setReferenceType(entry.getReferenceType());
            dto.setReferenceId(entry.getReferenceId());
            dto.setDebit(entry.getDebitAmount());
            dto.setCredit(entry.getCreditAmount());
            dto.setRunningBalance(runningBalance);
            dto.setRemarks(entry.getRemarks());

            // referenceNo set করা
            dto.setReferenceNo(resolveReferenceNo(entry));

            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    public SupplierDueSummaryDTO getSupplierDueSummary(Long supplierId) {
        Supplier supplier = supplierRepo.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        List<PartyLedgerEntry> entries =
                ledgerRepo.findByPartyTypeAndPartyIdOrderByEntryDateAscIdAsc(PartyType.SUPPLIER, supplierId);

        BigDecimal totalPurchase = BigDecimal.ZERO;
        BigDecimal totalPayment = BigDecimal.ZERO;

        for (PartyLedgerEntry entry : entries) {
            if (entry.getTransactionType() == LedgerTransactionType.PURCHASE) {
                totalPurchase = totalPurchase.add(entry.getCreditAmount());
            }
            if (entry.getTransactionType() == LedgerTransactionType.PAYMENT) {
                totalPayment = totalPayment.add(entry.getDebitAmount());
            }
        }

        BigDecimal currentDue = totalPurchase.subtract(totalPayment);

        SupplierDueSummaryDTO dto = new SupplierDueSummaryDTO();
        dto.setSupplierId(supplier.getId());
        dto.setSupplierName(supplier.getName());
        dto.setTotalPurchase(totalPurchase);
        dto.setTotalPayment(totalPayment);
        dto.setCurrentDue(currentDue);

        return dto;
    }

    private String resolveReferenceNo(PartyLedgerEntry entry) {
        if ("PURCHASE_ORDER".equals(entry.getReferenceType()) && entry.getReferenceId() != null) {
            PurchaseOrderHeader order = purchaseRepo.findById(entry.getReferenceId()).orElse(null);
            return order != null ? order.getInvoiceNumber() : null;
        }

        if ("SUPPLIER_PAYMENT".equals(entry.getReferenceType()) && entry.getReferenceId() != null) {
            SupplierPayment payment = paymentRepo.findById(entry.getReferenceId()).orElse(null);
            return payment != null ? payment.getVoucherNo() : null;
        }

        return null;
    }




    //get all due with supplier
    @Override
    public List<SupplierDueSummaryDTO> getAllSupplierDueSummaries() {
        List<Supplier> suppliers = supplierRepo.findAll();

        List<SupplierDueSummaryDTO> summaryList = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            List<PartyLedgerEntry> entries =
                    ledgerRepo.findByPartyTypeAndPartyIdOrderByEntryDateAscIdAsc(PartyType.SUPPLIER, supplier.getId());

            BigDecimal totalPurchase = BigDecimal.ZERO;
            BigDecimal totalPayment = BigDecimal.ZERO;

            for (PartyLedgerEntry entry : entries) {
                if (entry.getTransactionType() == LedgerTransactionType.PURCHASE) {
                    totalPurchase = totalPurchase.add(entry.getCreditAmount());
                }
                if (entry.getTransactionType() == LedgerTransactionType.PAYMENT) {
                    totalPayment = totalPayment.add(entry.getDebitAmount());
                }
            }

            BigDecimal currentDue = totalPurchase.subtract(totalPayment);

            SupplierDueSummaryDTO dto = new SupplierDueSummaryDTO();
            dto.setSupplierId(supplier.getId());
            dto.setSupplierName(supplier.getName());
            dto.setCompanyName(supplier.getCompanyName());
            dto.setMobileNumber(supplier.getMobileNumber());
            dto.setTotalPurchase(totalPurchase);
            dto.setTotalPayment(totalPayment);
            dto.setCurrentDue(currentDue);

            summaryList.add(dto);
        }

        return summaryList;
    }

}
