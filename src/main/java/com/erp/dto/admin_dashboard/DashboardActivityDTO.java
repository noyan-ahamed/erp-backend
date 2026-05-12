package com.erp.dto.admin_dashboard;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DashboardActivityDTO {
    private String type;
    private String title;
    private String referenceNo;
    private BigDecimal amount;
    private LocalDate date;
}