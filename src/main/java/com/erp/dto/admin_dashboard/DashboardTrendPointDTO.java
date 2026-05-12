package com.erp.dto.admin_dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardTrendPointDTO {
    private String label;
    private BigDecimal sales;
    private BigDecimal profit;
}
