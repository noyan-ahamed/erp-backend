package com.erp.enities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "purchase_order_items")
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    @JsonIgnoreProperties("items")
    private PurchaseOrderHeader purchaseOrderHeader;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private String unit;

    private Integer quantity;
    private BigDecimal unitPrice;
    @Column(name = "line_total")
    private BigDecimal lineTotal;

    // getters setters
}