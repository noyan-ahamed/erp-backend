package com.erp.services.implemented;

import com.erp.dto.PurchaseItemDTO;
import com.erp.dto.PurchaseOrderHeaderDTO;
import com.erp.enities.*;
import com.erp.enums.PurchaseStatus;
import com.erp.repositories.*;
import com.erp.services.LedgerService;
import com.erp.services.MailService;
import com.erp.services.PurchaseOrderService;
import com.erp.services.reportService.ReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImplement implements PurchaseOrderService {
    //supplier repo
    private final SupplierRepository supplierRepo;
    //invoice
    private final InvoiceNumberServiceImplement invoice;
    private final ProductRepository productRepo;
    private final PurchaseOrderHeaderRepository purchaseRepo;

    private final ProductStockRepository stockRepo;

    private final LedgerService ledgerService;

    private final ReportService reportService;
    private final MailService mailService;
    private final InventoryBatchRepository batchRepo;

    @Override
    @Transactional
    public PurchaseOrderHeader createPurchase(PurchaseOrderHeaderDTO dto) {

        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        PurchaseOrderHeader order = new PurchaseOrderHeader();
        order.setSupplier(supplier);
        order.setInvoiceNumber(invoice.generate("SUP"));

        BigDecimal total = BigDecimal.ZERO;

        List<PurchaseOrderItem> items = new ArrayList<>();

        for (PurchaseItemDTO itemDTO : dto.getItems()) {

            Product product = productRepo.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setPurchaseOrderHeader(order);
            item.setUnit(itemDTO.getUnit());

            BigDecimal lineTotal = itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            item.setLineTotal(lineTotal);

            total = total.add(lineTotal);
            items.add(item);
        }

        order.setItems(items);
        order.setTotalAmount(total);
        order.setStatus(PurchaseStatus.PENDING);
        order.setPaymentTerms(dto.getPaymentTerms());
        order.setCreated_at(LocalDate.now());


        return purchaseRepo.save(order);
    }

    @Transactional
    public PurchaseOrderHeader updateStatus(Long orderId, PurchaseStatus status) {
        PurchaseOrderHeader order = purchaseRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // RECEIVED ba CANCELED hoye gele ar status change kora jabe na
        if (order.getStatus() == PurchaseStatus.RECEIVED || order.getStatus() == PurchaseStatus.CANCELLED) {
            throw new RuntimeException("Order is already " + order.getStatus() + " and cannot be modified.");
        }

        order.setStatus(status);

        if (status == PurchaseStatus.RECEIVED) {

            for (PurchaseOrderItem item : order.getItems()) {

                Product product = item.getProduct();

                // STOCK UPDATE
                ProductStock stock = stockRepo.findByProduct(product)
                        .orElse(null);

                if (stock == null) {
                    stock = new ProductStock();
                    stock.setProduct(product);
                    stock.setQuantity(0);
                }

                stock.setQuantity(
                        stock.getQuantity() + item.getQuantity()
                );

                stockRepo.save(stock);

                // SELLING PRICE UPDATE
//                BigDecimal purchasePrice = item.getUnitPrice();

//                BigDecimal profit =
//                        purchasePrice.multiply(new BigDecimal("0.20"));

//                BigDecimal newSellingPrice =
//                        purchasePrice.add(profit);
//
//                product.setSellingPrice(newSellingPrice);

                productRepo.save(product);

                // FIFO BATCH CREATE
                InventoryBatch batch = new InventoryBatch();

                batch.setProduct(product);

                batch.setPurchaseItem(item);

                batch.setOriginalQuantity(item.getQuantity());

                batch.setRemainingQuantity(item.getQuantity());

                batch.setPurchasePrice(item.getUnitPrice());

                // SELLING PRICE
                BigDecimal purchasePrice = item.getUnitPrice();

                BigDecimal sellingPrice =
                        purchasePrice.multiply(new BigDecimal("1.20"));

                // update product display price
                product.setSellingPrice(sellingPrice);

                productRepo.save(product);

                // batch selling price
                batch.setSellingPrice(sellingPrice);

                batch.setReceivedDate(LocalDate.now());

                batchRepo.save(batch);
            }

            // ledger
            ledgerService.createSupplierPurchaseEntry(order);

            // mail
//            try {
//                sendPurchaseEmail(order);
//            } catch (Exception e) {
//                log.error("Mail send failed");
//            }
        }
        return purchaseRepo.save(order);
    }


    //    common method for sending mail
    @Override
    public void sendPurchaseEmail(PurchaseOrderHeader order) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("ORDER_ID", BigDecimal.valueOf(order.getId()));

//        generate jasper report
        byte[] pdf = reportService.generateReport("purchase_report", params);

        //get supplier mail
        String supplierEmail = order.getSupplier().getEmail();

        if (supplierEmail != null && !supplierEmail.isEmpty()) {
            String subject = "Purchase Order Confirmation - " + order.getInvoiceNumber();
            String body = "Dear " + order.getSupplier().getName() + ",\n\nPlease find the attached purchase order invoice.";

            mailService.sendEmailWithAttachment(supplierEmail, subject, body, pdf, "Invoice_" + order.getInvoiceNumber() + ".pdf");
        }
    }

    @Override
    public List<PurchaseOrderHeader> getAllOrders() {
        return purchaseRepo.findAll(
                Sort.by(Sort.Direction.DESC, "id")
        );
    }

    @Override
    public PurchaseOrderHeader getById(Long id) {
        return purchaseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public void delete(Long id) {
        purchaseRepo.deleteById(id);
    }

}
