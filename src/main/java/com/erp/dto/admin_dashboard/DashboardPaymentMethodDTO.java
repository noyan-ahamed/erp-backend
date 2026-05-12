package com.erp.dto.admin_dashboard;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardPaymentMethodDTO {
    private String paymentMethod;
    private BigDecimal totalAmount;
}