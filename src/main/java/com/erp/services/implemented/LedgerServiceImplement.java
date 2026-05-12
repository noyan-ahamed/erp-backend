package com.erp.services.implemented;

import com.erp.enities.*;
import com.erp.enums.LedgerTransactionType;
import com.erp.enums.PartyType;
import com.erp.repositories.PartyLedgerEntryRepository;
import com.erp.services.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerServiceImplement implements LedgerService {

    private final PartyLedgerEntryRepository ledgerRepo;

    @Override
    public void createSupplierPurchaseEntry(PurchaseOrderHeader order) {
        PartyLedgerEntry entry = new PartyLedgerEntry();
        entry.setEntryDate(LocalDate.now());
        entry.setPartyType(PartyType.SUPPLIER);
        entry.setPartyId(order.getSupplier().getId());
        entry.setTransactionType(LedgerTransactionType.PURCHASE);
        entry.setReferenceType("PURCHASE_ORDER");
        entry.setReferenceId(order.getId());
        entry.setDebitAmount(BigDecimal.ZERO);
        entry.setCreditAmount(order.getTotalAmount());
        entry.setRemarks("Purchase received - " + order.getInvoiceNumber());

        ledgerRepo.save(entry);

    }

    @Override
    public void createSupplierPaymentEntry(SupplierPayment payment) {
        PartyLedgerEntry entry = new PartyLedgerEntry();
        entry.setEntryDate(payment.getPaymentDate());
        entry.setPartyType(PartyType.SUPPLIER);
        entry.setPartyId(payment.getSupplier().getId());
        entry.setTransactionType(LedgerTransactionType.PAYMENT);
        entry.setReferenceType("SUPPLIER_PAYMENT");
        entry.setReferenceId(payment.getId());
        entry.setDebitAmount(payment.getAmount());
        entry.setCreditAmount(BigDecimal.ZERO);
        entry.setRemarks("Supplier payment - " + payment.getVoucherNo());

        ledgerRepo.save(entry);

    }

    @Override
    public List<PartyLedgerEntry> getAllLedger() {
        return ledgerRepo.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

//new

    @Override
    public void createCustomerSaleEntry(SalesOrderHeader salesOrder) {
        PartyLedgerEntry entry = new PartyLedgerEntry();
        entry.setEntryDate(salesOrder.getSalesDate());
        entry.setPartyType(PartyType.CUSTOMER);
        entry.setPartyId(salesOrder.getCustomer().getId());
        entry.setTransactionType(LedgerTransactionType.SALE);
        entry.setReferenceType("SALES_ORDER");
        entry.setReferenceId(salesOrder.getId());
        entry.setDebitAmount(BigDecimal.ZERO);
        entry.setCreditAmount(salesOrder.getNetTotal());
        entry.setRemarks("Sale invoice - " + salesOrder.getInvoiceNumber());
        ledgerRepo.save(entry);
    }

    @Override
    public void createCustomerPaymentEntry(CustomerPayment payment) {

        boolean alreadyExists = ledgerRepo
                .findByReferenceTypeAndReferenceId("CUSTOMER_PAYMENT", payment.getId())
                .isPresent();

        if (alreadyExists) {
            return; // duplicate ledger block
        }

        PartyLedgerEntry entry = new PartyLedgerEntry();
        entry.setEntryDate(payment.getPaymentDate());
        entry.setPartyType(PartyType.CUSTOMER);
        entry.setPartyId(payment.getCustomer().getId());
        entry.setTransactionType(LedgerTransactionType.PAYMENT);
        entry.setReferenceType("CUSTOMER_PAYMENT");
        entry.setReferenceId(payment.getId());
        entry.setDebitAmount(payment.getAmount());
        entry.setCreditAmount(BigDecimal.ZERO);
        entry.setRemarks("Customer payment - " + payment.getVoucherNo());

        ledgerRepo.save(entry);
    }


}
