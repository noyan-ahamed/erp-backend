package com.erp.dto.admin_dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardTopCustomerDTO {
    private Long customerId;
    private String customerName;
    private String mobileNumber;
    private BigDecimal totalPurchaseAmount;
}