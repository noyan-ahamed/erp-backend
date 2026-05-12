package com.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesItemRequestDTO {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice; // optional override from frontend if needed
}
