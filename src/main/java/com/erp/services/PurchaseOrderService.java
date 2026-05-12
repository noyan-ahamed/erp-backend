package com.erp.services;

import com.erp.dto.PurchaseOrderHeaderDTO;
import com.erp.enities.PurchaseOrderHeader;
import com.erp.enums.PurchaseStatus;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderHeader createPurchase(PurchaseOrderHeaderDTO dto);

    PurchaseOrderHeader updateStatus(Long orderId, PurchaseStatus status);

    List<PurchaseOrderHeader> getAllOrders();

    PurchaseOrderHeader getById(Long id);

    void delete(Long id);

    void sendPurchaseEmail(PurchaseOrderHeader order)  throws Exception;
}
