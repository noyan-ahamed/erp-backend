package com.erp.dto.admin_dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardTopProductDTO {
    private Long productId;
    private String productName;
    private String sku;
    private Integer totalSoldQty;
    private BigDecimal totalSoldAmount;
}
