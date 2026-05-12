package com.erp.dto.admin_dashboard;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardSummaryDTO {
    private BigDecimal todaySales;
    private BigDecimal monthSales;
    private BigDecimal todayProfit;
    private BigDecimal monthProfit;
    private BigDecimal totalCustomerDue;
    private BigDecimal totalSupplierPayable;
    private Long lowStockCount;
    private Long totalProducts;
}