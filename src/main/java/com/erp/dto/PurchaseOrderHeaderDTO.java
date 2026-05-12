package com.erp.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PurchaseOrderHeaderDTO {

        @NotNull(message = "Supplier id is required")
        private Long supplierId;

        private String paymentTerms;

        @NotEmpty(message = "Item list cannot be empty")
        private List<PurchaseItemDTO> items;

        // getters setters

}
