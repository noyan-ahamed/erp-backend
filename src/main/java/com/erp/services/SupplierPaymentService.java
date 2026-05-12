package com.erp.services;

import com.erp.dto.SupplierPaymentRequestDTO;
import com.erp.dto.SupplierPaymentResponseDTO;
import com.erp.enities.SupplierPayment;

import java.util.List;

public interface SupplierPaymentService {
    SupplierPaymentResponseDTO savePayment(SupplierPaymentRequestDTO dto);

    List<SupplierPaymentResponseDTO> getPaymentsBySupplier(Long supplierId);
}
