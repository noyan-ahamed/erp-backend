package com.erp.dto;

import com.erp.enums.CustomerPaymentStatus;
import com.erp.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerPaymentResponseDTO {
    private Long id;
    private String voucherNo;
    private Long customerId;
    private String customerName;
    private String customerMobile;
    private String salesInvoiceNumber;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private String remarks;
    private CustomerPaymentStatus status;
}
