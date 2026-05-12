package com.erp.controllers;

import com.erp.dto.SupplierPaymentRequestDTO;
import com.erp.dto.SupplierPaymentResponseDTO;
import com.erp.services.SupplierPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier-payments")
@RequiredArgsConstructor
public class SupplierPaymentController {

    private final SupplierPaymentService supplierPaymentService;

    @PostMapping
    public SupplierPaymentResponseDTO savePayment(@RequestBody SupplierPaymentRequestDTO dto) {
        return supplierPaymentService.savePayment(dto);
    }

    @GetMapping("/supplier/{supplierId}")
    public List<SupplierPaymentResponseDTO> getPaymentsBySupplier(@PathVariable Long supplierId) {
        return supplierPaymentService.getPaymentsBySupplier(supplierId);
    }
}
