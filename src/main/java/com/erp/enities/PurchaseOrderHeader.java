package com.erp.enities;

import com.erp.enums.PurchaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "purchase_orders")
public class PurchaseOrderHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private BigDecimal totalAmount;
    @Column(name = "payment_terms")
    private String paymentTerms;


    @Column(name = "created_at")
    private LocalDate created_at;

    @OneToMany(mappedBy = "purchaseOrderHeader",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnoreProperties("purchaseOrderHeader")
    private List<PurchaseOrderItem> items;

    // getters setters
}
