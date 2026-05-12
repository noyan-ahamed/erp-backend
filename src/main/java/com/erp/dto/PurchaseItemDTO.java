package com.erp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseItemDTO {

    @NotNull(message = "Product id required")
    private Long productId;

    @NotNull(message = "Quantity required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String unit;

    @NotNull(message = "Price required")
    @Positive(message = "Price must be positive")
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    // getters setters

}
