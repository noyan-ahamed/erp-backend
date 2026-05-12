package com.erp.dto.admin_dashboard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardLowStockItemDTO {
    private Long productId;
    private String productName;
    private String sku;
    private Integer stock;
    private Integer minStockLevel;
}