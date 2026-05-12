package com.erp.enities;

import com.erp.enums.ProductCategoryStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "product_categories")
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProductCategoryStatus status;

    @Formula("(SELECT COUNT(*) FROM products p WHERE p.category_id = id)")
    private int productCount;

    @OneToMany(mappedBy = "category")
//    @JsonManagedReference
    @JsonIgnoreProperties("category")
    private List<Product> products;

    // getters setters
}
