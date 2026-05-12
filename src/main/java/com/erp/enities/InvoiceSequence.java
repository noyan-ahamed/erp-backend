package com.erp.enities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "invoice_sequences")
public class InvoiceSequence {

    @Id
    private String sequenceKey;
    // e.g. PURCHASE, SALES, CUSTOMER_ORDER

    private Long currentValue;

    // getters setters
}
