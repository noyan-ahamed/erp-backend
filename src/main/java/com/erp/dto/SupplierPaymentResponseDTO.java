package com.erp.dto;

import com.erp.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SupplierPaymentResponseDTO {

    private Long id;
    private String voucherNo;
    private String supplierName;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private String remarks;
    private String purchaseInvoiceNumber; // optional
}