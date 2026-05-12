package com.erp.dto;

import com.erp.enities.ProductCategory;
import com.erp.enities.ProductStock;
import com.erp.enities.Supplier;
import com.erp.enums.ProductStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ProductDTO {

@NotNull(message = "Product Name is Required")
    private String name;

@NotNull(message = "SKU code is Required")
    private String sku;

    private String unit;

    private Integer minStockLevel;



    private ProductStatus status;

    private LocalDate created_at;


    private ProductCategory category;

}
