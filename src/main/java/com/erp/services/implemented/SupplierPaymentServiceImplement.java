package com.erp.services.implemented;

import com.erp.dto.SupplierDueSummaryDTO;
import com.erp.enities.PartyLedgerEntry;
import com.erp.enities.SupplierPayment;
import com.erp.enums.LedgerTransactionType;
import com.erp.enums.PartyType;
import com.erp.repositories.PartyLedgerEntryRepository;
import com.erp.services.SupplierLedgerService;
import com.erp.services.SupplierPaymentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.erp.dto.SupplierPaymentRequestDTO;
import com.erp.dto.SupplierPaymentResponseDTO;
import com.erp.enities.PurchaseOrderHeader;
import com.erp.enities.Supplier;
import com.erp.repositories.PurchaseOrderHeaderRepository;
import com.erp.repositories.SupplierPaymentRepository;
import com.erp.repositories.SupplierRepository;

import lombok.RequiredArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierPaymentServiceImplement implements SupplierPaymentService {

    private final SupplierPaymentRepository paymentRepo;
    private final SupplierRepository supplierRepo;
    private final PurchaseOrderHeaderRepository purchaseRepo;
    private final LedgerServiceImplement ledgerService;
    private final InvoiceNumberServiceImplement invoice;
    private final SupplierLedgerService supplierLedgerService;
    private final PartyLedgerEntryRepository ledgerRepo;

    @Override
    @Transactional
    public SupplierPaymentResponseDTO savePayment(SupplierPaymentRequestDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().signum() <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero.");
        }

        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        PurchaseOrderHeader purchaseOrder = null;
        if (dto.getPurchaseOrderId() != null) {
            purchaseOrder = purchaseRepo.findById(dto.getPurchaseOrderId())
                    .orElseThrow(() -> new RuntimeException("Purchase order not found"));
        }

        SupplierPayment payment = new SupplierPayment();
        payment.setVoucherNo(invoice.generate("PAY"));
        payment.setSupplier(supplier);
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setRemarks(dto.getRemarks());
        payment.setPurchaseOrder(purchaseOrder);
        SupplierDueSummaryDTO dueSummary =
                supplierLedgerService.getSupplierDueSummary(dto.getSupplierId());

        if (dto.getAmount().compareTo(dueSummary.getCurrentDue()) > 0) {
            throw new RuntimeException("Payment amount exceeds supplier due.");
        }
        if (dto.getPaymentDate() == null) {
            dto.setPaymentDate(LocalDate.now());
        }

        SupplierPayment saved = paymentRepo.save(payment);

        // 🔥 ledger entry auto create
        ledgerService.createSupplierPaymentEntry(saved);

        return mapToResponse(saved);
    }

    @Override
    public List<SupplierPaymentResponseDTO> getPaymentsBySupplier(Long supplierId) {
        return paymentRepo.findBySupplierIdOrderByPaymentDateAscIdAsc(supplierId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SupplierPaymentResponseDTO mapToResponse(SupplierPayment payment) {
        SupplierPaymentResponseDTO dto = new SupplierPaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setVoucherNo(payment.getVoucherNo());
        dto.setSupplierName(payment.getSupplier().getName());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setRemarks(payment.getRemarks());

        if (payment.getPurchaseOrder() != null) {
            dto.setPurchaseInvoiceNumber(payment.getPurchaseOrder().getInvoiceNumber());
        }

        return dto;
    }


}
