package com.erp.services;

import com.erp.enities.CustomerPayment;
import com.erp.enities.PurchaseOrderHeader;
import com.erp.enities.SalesOrderHeader;
import com.erp.enums.PurchaseStatus;

public interface InvoiceDeliveryService {
    void generateInvoice(SalesOrderHeader salesOrder);
    void printInvoice(SalesOrderHeader salesOrder);
    void sendPurchaseStatusEmail(PurchaseOrderHeader order, PurchaseStatus statusType);

    void notifySellerPaymentApproved(CustomerPayment payment);
}
