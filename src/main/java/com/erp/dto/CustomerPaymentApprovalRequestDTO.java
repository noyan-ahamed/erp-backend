package com.erp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerPaymentApprovalRequestDTO {
    private Long approvedByUserId; // future auth ready
    private String remarks;
}
