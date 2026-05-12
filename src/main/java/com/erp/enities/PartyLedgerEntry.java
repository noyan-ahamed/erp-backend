package com.erp.enities;

import com.erp.enums.LedgerTransactionType;
import com.erp.enums.PartyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "party_ledger_entries")
public class PartyLedgerEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private LocalDate entryDate;

    @Enumerated(EnumType.STRING)
    private PartyType partyType; // SUPPLIER / CUSTOMER / EMPLOYEE

    private Long partyId; // supplierId / customerId / employeeId

    @Enumerated(EnumType.STRING)
    private LedgerTransactionType transactionType;

    private String referenceType; // PURCHASE_ORDER, PAYMENT, SALE_ORDER, RETURN
    private Long referenceId;     // purchaseOrder id, payment id etc.

    @Column(precision = 15, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO;

    private String remarks;
}
