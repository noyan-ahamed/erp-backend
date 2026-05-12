package com.erp.services;

import com.erp.dto.admin_dashboard.DashboardResponseDTO;

import java.time.LocalDate;

public interface DashboardService {
    DashboardResponseDTO getDashboardData(String filterType, LocalDate fromDate, LocalDate toDate);
}