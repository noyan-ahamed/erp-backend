package com.erp.dto;

import com.erp.enities.InventoryBatch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumedBatchDTO {

    private InventoryBatch batch;

    private Integer quantity;
}