package com.erp.services;

import com.erp.dto.ConsumedBatchDTO;
import com.erp.enities.Product;

import java.util.List;

public interface InventoryService {

    List<ConsumedBatchDTO> consumeStock(Product product, Integer sellQty);
}