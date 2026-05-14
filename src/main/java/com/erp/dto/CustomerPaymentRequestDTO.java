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

    // optional invoice payment
    private Long salesOrderId;

    private BigDecimal amount;

    private LocalDate paymentDate;

    private PaymentMethod paymentMethod;

    private String remarks;
}