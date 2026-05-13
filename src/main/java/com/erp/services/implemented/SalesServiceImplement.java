package com.erp.services.implemented;

import com.erp.config.SecurityUtil;
import com.erp.dto.*;
import com.erp.enities.*;
import com.erp.enums.CustomerPaymentStatus;
import com.erp.enums.PaymentMethod;
import com.erp.enums.SalesStatus;
import com.erp.repositories.*;
import com.erp.services.InventoryService;
import com.erp.services.InvoiceDeliveryService;
import com.erp.services.LedgerService;
import com.erp.services.SalesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesServiceImplement implements SalesService {

    private final SalesOrderHeaderRepository salesOrderHeaderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final LedgerService ledgerService;
    private final InvoiceNumberServiceImplement invoiceNumberService;
    private final InvoiceDeliveryService invoiceDeliveryService;
    private final InventoryService inventoryService;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public SalesResponseDTO createSale(SalesCreateRequestDTO request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Sale must contain at least one item.");
        }

        Customer customer = resolveCustomer(request);

        Users currentUser = securityUtil.getCurrentUser();

        if (currentUser.getEmployee() == null) {
            throw new RuntimeException("Current user is not linked with employee.");
        }

        Employee seller = currentUser.getEmployee();

        BigDecimal subTotal = BigDecimal.ZERO;
        List<SalesOrderItem> itemEntities = new ArrayList<>();

        SalesOrderHeader salesOrder = new SalesOrderHeader();
        salesOrder.setInvoiceNumber(invoiceNumberService.generate("SAL"));
        salesOrder.setCustomer(customer);
        salesOrder.setSellerEmployee(seller);
        salesOrder.setSalesDate(request.getSalesDate() != null ? request.getSalesDate() : LocalDate.now());
        salesOrder.setRemarks(request.getRemarks());
        salesOrder.setStatus(SalesStatus.COMPLETED);

        for (SalesItemRequestDTO itemDTO : request.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));

            ProductStock stock = productStockRepository.findByProduct(product)
                    .orElseThrow(() -> new RuntimeException("Stock record not found for product: " + product.getName()));

            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                throw new RuntimeException("Quantity must be greater than zero.");
            }

            if (stock.getQuantity() < itemDTO.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

//            if (product.getSellingPrice() == null || product.getSellingPrice().compareTo(BigDecimal.ZERO) <= 0) {
//                throw new RuntimeException("Selling price not set for product: " + product.getName());
//            }
//
//            BigDecimal unitPrice = product.getSellingPrice();
//            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
//
//            SalesOrderItem item = new SalesOrderItem();
//            item.setSalesOrder(salesOrder);
//            item.setProduct(product);
//            item.setQuantity(itemDTO.getQuantity());
//            item.setUnitPrice(unitPrice);
//            item.setLineTotal(lineTotal);
//
//            itemEntities.add(item);
//            subTotal = subTotal.add(lineTotal);
//
//            // stock deduction
//            inventoryService.consumeStock(
//                    product,
//                    itemDTO.getQuantity()
//            );

            // FIFO stock deduction + batch fetch
            List<ConsumedBatchDTO> consumedBatches =
                    inventoryService.consumeStock(
                            product,
                            itemDTO.getQuantity()
                    );

            for (ConsumedBatchDTO consumed : consumedBatches) {

                InventoryBatch batch = consumed.getBatch();

                BigDecimal unitPrice = batch.getSellingPrice();

                BigDecimal lineTotal =
                        unitPrice.multiply(
                                BigDecimal.valueOf(consumed.getQuantity())
                        );

                SalesOrderItem item = new SalesOrderItem();

                item.setSalesOrder(salesOrder);

                item.setProduct(product);

                item.setQuantity(consumed.getQuantity());

                item.setUnitPrice(unitPrice);

                item.setLineTotal(lineTotal);

                itemEntities.add(item);

                subTotal = subTotal.add(lineTotal);
            }
        }

        BigDecimal discount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
//        discountAmount = subTotal * percent / 100
        BigDecimal netTotal = subTotal.subtract(discount);

        if (netTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Discount cannot exceed subtotal.");
        }

        BigDecimal paidAmount = request.getPaidAmount() != null ? request.getPaidAmount() : BigDecimal.ZERO;

        if (paidAmount.compareTo(netTotal) > 0) {
            throw new RuntimeException("Paid amount cannot exceed net total.");
        }

        BigDecimal dueAmount = netTotal.subtract(paidAmount);

        salesOrder.setSubTotal(subTotal);
        salesOrder.setDiscountAmount(discount);
        salesOrder.setNetTotal(netTotal);
        salesOrder.setPaidAmount(paidAmount);
        salesOrder.setDueAmount(dueAmount);

        SalesOrderHeader savedOrder = salesOrderHeaderRepository.save(salesOrder);

        for (SalesOrderItem item : itemEntities) {
            item.setSalesOrder(savedOrder);
        }
        salesOrderItemRepository.saveAll(itemEntities);
        savedOrder.setItems(itemEntities);

        // ledger entry for sale
        ledgerService.createCustomerSaleEntry(savedOrder);

        // if immediate payment is made at sale time => auto approved
        if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            CustomerPayment payment = new CustomerPayment();
            payment.setVoucherNo(invoiceNumberService.generate("RCV"));
            payment.setCustomer(customer);
            payment.setSalesOrder(savedOrder);
            payment.setReceivedByEmployee(seller);
            payment.setAmount(paidAmount);
            payment.setPaymentDate(savedOrder.getSalesDate());
            payment.setPaymentMethod(PaymentMethod.CASH); // can later expose from frontend if needed
            payment.setRemarks("Payment received during sale - " + savedOrder.getInvoiceNumber());
            payment.setStatus(CustomerPaymentStatus.APPROVED);

            CustomerPayment savedPayment = customerPaymentRepository.save(payment);
            ledgerService.createCustomerPaymentEntry(savedPayment);
        }

        // placeholders
        invoiceDeliveryService.generateInvoice(savedOrder);

        return mapToSalesResponse(savedOrder);
    }

    @Override
    public SalesResponseDTO getSaleById(Long id) {
        SalesOrderHeader salesOrder = salesOrderHeaderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found."));
        return mapToSalesResponse(salesOrder);
    }

    @Override
    public List<SalesResponseDTO> getAllSales() {
        return salesOrderHeaderRepository.findAll().stream()
                .map(this::mapToSalesResponse)
                .toList();
    }

    @Override
    public List<CustomerSearchResponseDTO> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return customerRepository.findByNameContainingIgnoreCaseOrMobileNumberContaining(keyword, keyword)
                .stream()
                .map(customer -> {
                    CustomerSearchResponseDTO dto = new CustomerSearchResponseDTO();
                    dto.setId(customer.getId());
                    dto.setName(customer.getName());
                    dto.setEmail(customer.getEmail());
                    dto.setCompanyName(customer.getCompanyName());
                    dto.setMobileNumber(customer.getMobileNumber());
                    dto.setAddress(customer.getAddress());
                    return dto;
                })
                .toList();
    }

    private Customer resolveCustomer(SalesCreateRequestDTO request) {
        if (request.getCustomerId() != null) {
            return customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found."));
        }

        if (request.getNewCustomer() != null) {
            QuickCustomerCreateRequestDTO c = request.getNewCustomer();

            if (c.getMobileNumber() == null || c.getMobileNumber().isBlank()) {
                throw new RuntimeException("Customer mobile number is required.");
            }

            customerRepository.findByMobileNumber(c.getMobileNumber()).ifPresent(existing -> {
                throw new RuntimeException("Customer with this mobile already exists.");
            });

            Customer customer = new Customer();
            customer.setName(c.getName());
            customer.setCompanyName(c.getCompanyName());
            customer.setMobileNumber(c.getMobileNumber());
            customer.setAddress(c.getAddress());

            return customerRepository.save(customer);
        }

        throw new RuntimeException("Customer information is required.");
    }

    private SalesResponseDTO mapToSalesResponse(SalesOrderHeader salesOrder) {
        SalesResponseDTO dto = new SalesResponseDTO();
        dto.setSalesId(salesOrder.getId());
        dto.setInvoiceNumber(salesOrder.getInvoiceNumber());
        dto.setSalesDate(salesOrder.getSalesDate());
        dto.setCustomerId(salesOrder.getCustomer().getId());
        dto.setCustomerName(salesOrder.getCustomer().getName());
        dto.setCustomerMobile(salesOrder.getCustomer().getMobileNumber());

        if (salesOrder.getSellerEmployee() != null) {
            dto.setSellerEmployeeId(salesOrder.getSellerEmployee().getId());
            dto.setSellerEmployeeName(salesOrder.getSellerEmployee().getName());
        }

        dto.setSubTotal(salesOrder.getSubTotal());
        dto.setDiscountAmount(salesOrder.getDiscountAmount());
        dto.setNetTotal(salesOrder.getNetTotal());
        dto.setPaidAmount(salesOrder.getPaidAmount());
        dto.setDueAmount(salesOrder.getDueAmount());
        dto.setRemarks(salesOrder.getRemarks());

        List<SalesOrderItemResponseDTO> items = salesOrder.getItems() != null
                ? salesOrder.getItems().stream().map(item -> {
            SalesOrderItemResponseDTO itemDTO = new SalesOrderItemResponseDTO();
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setSku(item.getProduct().getSku());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setLineTotal(item.getLineTotal());
            return itemDTO;
        }).toList()
                : new ArrayList<>();

        dto.setItems(items);
        return dto;
    }
}
