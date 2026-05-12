package com.erp.enities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "inventory_batches")
public class InventoryBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //whice product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    //which purchase item
    @ManyToOne
    @JoinColumn(name = "purchase_item_id")
    private PurchaseOrderItem purchaseItem;

    // original qty
    private Integer originalQuantity;

    private Integer remainingQuantity;

    // purchase price
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;

    // received date
    private LocalDate receivedDate;
}