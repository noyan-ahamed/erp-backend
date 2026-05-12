package com.erp.dto;

import com.erp.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerPaymentRequestDTO {
    private Long customerId;
    private Long salesOrderId; // optional
    private Long receivedByEmployeeId; // optional now, useful later
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private String remarks;
}
