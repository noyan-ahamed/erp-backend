package com.erp.controllers;


import com.erp.dto.PurchaseOrderHeaderDTO;
import com.erp.enities.PurchaseOrderHeader;
import com.erp.enums.PurchaseStatus;
import com.erp.services.InvoiceDeliveryService;
import com.erp.services.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseService;
    private final InvoiceDeliveryService invoiceDeliveryService;

    // Create Purchase Order
    @PostMapping
    public ResponseEntity<PurchaseOrderHeader> createPurchase(
            @RequestBody PurchaseOrderHeaderDTO dto
    ) {
        return ResponseEntity.ok(purchaseService.createPurchase(dto));
    }

    // Get All Purchase Orders
    @GetMapping
    public ResponseEntity<List<PurchaseOrderHeader>> getAllOrders() {
        return ResponseEntity.ok(purchaseService.getAllOrders());
    }

    // Get Single Purchase Order
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderHeader> getOrderById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(purchaseService.getById(id));
    }

    // Update Status
    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrderHeader> updateStatus(
            @PathVariable Long id,
            @RequestParam PurchaseStatus status
    ) {
        return ResponseEntity.ok(purchaseService.updateStatus(id, status));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(
            @PathVariable Long id
    ) {
        purchaseService.delete(id);
        return ResponseEntity.ok("Purchase order deleted");
    }


    //for manually mailing
    @PostMapping("/{id}/send-email")
    public ResponseEntity<String> manualEmailSend(@PathVariable Long id) {
        try {
            PurchaseOrderHeader order = purchaseService.getById(id);
            PurchaseStatus status = order.getStatus();
            // সার্ভিস লেয়ারের মেইল মেথডটি কল করা
            invoiceDeliveryService.sendPurchaseStatusEmail(order,status);
            return ResponseEntity.ok("Email sent successfully to " + order.getSupplier().getName());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

}