package com.erp.dto;

import com.erp.enums.PurchaseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseStatusUpdateDTO {
    @NotNull(message = "Status is required")
    private PurchaseStatus status;

    // getters setters
}
