package com.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerDueSummaryDTO {
    private Long customerId;
    private String customerName;
    private String mobileNumber;
    private String companyName;
    private String address;

    private BigDecimal totalSales;
    private BigDecimal totalApprovedPayment;
    private BigDecimal currentDue;

    private LocalDate lastPaymentDate;
    private LocalDate lastSaleDate;
}
