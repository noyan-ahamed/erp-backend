package com.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SalesCreateRequestDTO {
    private Long customerId;
    private QuickCustomerCreateRequestDTO newCustomer; // optional quick create

    private Long sellerEmployeeId; // future auth compatible
    private LocalDate salesDate;
    private BigDecimal discountAmount;
    private BigDecimal paidAmount;
    private String remarks;

    private List<SalesItemRequestDTO> items;
}
