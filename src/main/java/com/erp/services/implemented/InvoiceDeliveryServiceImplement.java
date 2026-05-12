package com.erp.services.implemented;

import com.erp.enities.CustomerPayment;
import com.erp.enities.PurchaseOrderHeader;
import com.erp.enities.SalesOrderHeader;
import com.erp.enums.PurchaseStatus;
import com.erp.services.InvoiceDeliveryService;
import com.erp.services.MailService;
import com.erp.services.reportService.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceDeliveryServiceImplement implements InvoiceDeliveryService {

//    private JavaMailSender javaMailSender;
    private final ReportService reportService;

    private final MailService mailService;
    @Override
    public void generateInvoice(SalesOrderHeader salesOrder) {
        log.info("Invoice generation placeholder for sale invoice: {}", salesOrder.getInvoiceNumber());
    }

    @Override
    public void printInvoice(SalesOrderHeader salesOrder) {
        log.info("Invoice print placeholder for sale invoice: {}", salesOrder.getInvoiceNumber());
    }


    @Override
    public void sendPurchaseStatusEmail(PurchaseOrderHeader order, PurchaseStatus statusType) {
        try {
            String supplierEmail = order.getSupplier().getEmail();
            if (supplierEmail == null || supplierEmail.isEmpty()) {
                log.warn("Supplier email not found for order: {}", order.getInvoiceNumber());
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("ORDER_ID", BigDecimal.valueOf(order.getId()));

            // রিপোর্ট জেনারেট (সব স্ট্যাটাসেই ইনভয়েস/পিও কপি পাঠানো ভালো)
            byte[] pdf = reportService.generateReport("purchase_report", params);

            String subject = "";
            String body = "";
            String fileName = "PO_" + order.getInvoiceNumber() + ".pdf";

            switch (statusType) {
                case PENDING:
                    subject = "New Purchase Order Created - " + order.getInvoiceNumber();
                    body = "Dear " + order.getSupplier().getName() + ",\n\n" +
                            "A new purchase order has been created and is currently PENDING. " +
                            "Please check the attached requirements.\n\n" +
                            "Order No: " + order.getInvoiceNumber() + "\n" +
                            "Status: PENDING";
                    break;

                case RECEIVED:
                    subject = "Order Received & Confirmed - " + order.getInvoiceNumber();
                    body = "Dear " + order.getSupplier().getName() + ",\n\n" +
                            "We have successfully RECEIVED the items for Order No: " + order.getInvoiceNumber() + ". " +
                            "The stock has been updated in our system.\n\n" +
                            "Thank you for your service.";
                    break;

                case CANCELLED:
                    subject = "Order Cancelled - " + order.getInvoiceNumber();
                    body = "Dear " + order.getSupplier().getName() + ",\n\n" +
                            "The Purchase Order No: " + order.getInvoiceNumber() + " has been CANCELLED.\n" +
                            "If you have any queries, please contact our procurement department.";
                    break;
            }

            mailService.sendEmailWithAttachment(supplierEmail, subject, body, pdf, fileName);
            log.info("Email sent for status: {} to {}", statusType, supplierEmail);

        } catch (Exception e) {
            log.error("Failed to send {} email: {}", statusType, e.getMessage());
        }
    }

    @Override
    public void notifySellerPaymentApproved(CustomerPayment payment) {
        // TODO:
        // 1. seller / receiver employee notification
        // 2. customer email / sms notification
        // 3. payment approval mail template
        log.info("Customer payment approved notification placeholder for voucher: {}", payment.getVoucherNo());
    }
}
