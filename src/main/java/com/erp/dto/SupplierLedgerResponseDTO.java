package com.erp.dto;

import com.erp.enums.LedgerTransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SupplierLedgerResponseDTO {

    private LocalDate date;
    private LedgerTransactionType transactionType;
    private String referenceType;
    private Long referenceId;
    private String referenceNo; // invoice no / voucher no
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal runningBalance;
    private String remarks;
}