package com.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmployeeSalesSummaryDTO {
    private Long employeeId;
    private String employeeName;
    private BigDecimal totalSales;
    private BigDecimal totalCollected;
    private BigDecimal totalDue;
}
