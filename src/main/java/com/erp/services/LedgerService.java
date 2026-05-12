package com.erp.services;

import com.erp.enities.*;

import java.util.List;

public interface LedgerService {

    void createSupplierPurchaseEntry(PurchaseOrderHeader order);

    void createSupplierPaymentEntry(SupplierPayment payment);

    // NEW
    void createCustomerSaleEntry(SalesOrderHeader salesOrder);
    void createCustomerPaymentEntry(CustomerPayment payment);

    List<PartyLedgerEntry> getAllLedger();
}
