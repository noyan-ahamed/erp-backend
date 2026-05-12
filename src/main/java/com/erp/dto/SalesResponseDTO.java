package com.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SalesResponseDTO {
    private Long salesId;
    private String invoiceNumber;
    private LocalDate salesDate;

    private Long customerId;
    private String customerName;
    private String customerMobile;

    private Long sellerEmployeeId;
    private String sellerEmployeeName;

    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal netTotal;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;

    private String remarks;

    private List<SalesOrderItemResponseDTO> items;
}
