package com.erp.dto.admin_dashboard;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardResponseDTO {
    private DashboardSummaryDTO summary;
    private List<DashboardTrendPointDTO> salesProfitTrend;
    private List<DashboardTrendPointDTO> monthlySalesComparison;
    private List<DashboardPaymentMethodDTO> paymentMethodDistribution;
    private List<DashboardLowStockItemDTO> lowStockItems;
    private List<DashboardActivityDTO> recentActivities;
    private List<DashboardTopCustomerDTO> topCustomers;
    private List<DashboardTopProductDTO> topProducts;
}