package com.erp.enities;

import com.erp.enums.SalesStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "sales_orders")
public class SalesOrderHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // future auth compatible
    @ManyToOne
    @JoinColumn(name = "seller_employee_id", nullable = true)
    private Employee sellerEmployee;

    @Column(nullable = false)
    private LocalDate salesDate;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal netTotal = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal dueAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private SalesStatus status = SalesStatus.COMPLETED;

    private String remarks;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SalesOrderItem> items;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        if (this.salesDate == null) {
            this.salesDate = LocalDate.now();
        }
    }
}
