package com.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerLedgerResponseDTO {
    private LocalDate date;
    private Long referenceId;
    private String referenceNo;
    private String referenceType;
    private String transactionType;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal runningBalance;
    private String remarks;
}
