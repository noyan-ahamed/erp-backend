package com.erp.enities;

import com.erp.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String sku;

//    @Column(name = "purchase_price")
//    private BigDecimal purchasePrice;
    @Column(name = "selling_price")
    private BigDecimal sellingPrice;


    @Formula("(SELECT COALESCE(ps.quantity,0) FROM product_stocks ps WHERE ps.product_id = id)")
    private Integer stock;

    @JsonManagedReference
    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private ProductStock productStock;


    @Column(name = "min_stock_level")
    private Integer minStockLevel;


    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    @Column(name = "created_at")
    private LocalDate created_at;

    @ManyToOne
    @JoinColumn(name = "category_id")
//    @JsonBackReference
    @JsonIgnoreProperties("products")
    private ProductCategory category;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // getters setters

}

