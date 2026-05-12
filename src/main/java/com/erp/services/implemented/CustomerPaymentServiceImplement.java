package com.erp.services.implemented;

import com.erp.dto.CustomerDueSummaryDTO;
import com.erp.dto.CustomerPaymentApprovalRequestDTO;
import com.erp.dto.CustomerPaymentRequestDTO;
import com.erp.dto.CustomerPaymentResponseDTO;
import com.erp.enities.*;
import com.erp.enums.CustomerPaymentStatus;
import com.erp.enums.PartyType;
import com.erp.repositories.*;
import com.erp.services.CustomerPaymentService;
import com.erp.services.InvoiceDeliveryService;
import com.erp.services.LedgerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerPaymentServiceImplement implements CustomerPaymentService {

    private final CustomerPaymentRepository customerPaymentRepository;
    private final CustomerRepository customerRepository;
    private final SalesOrderHeaderRepository salesOrderHeaderRepository;
    private final EmployeeRepository employeeRepository;
    private final UsersRepository usersRepository;
    private final LedgerService ledgerService;
    private final InvoiceNumberServiceImplement invoiceNumberService;
    private final InvoiceDeliveryService invoiceDeliveryService;
    private final PartyLedgerEntryRepository ledgerRepository;


    private BigDecimal getCurrentCustomerDue(Long customerId) {
        List<com.erp.enities.PartyLedgerEntry> entries = ledgerRepository.findByPartyTypeAndPartyId(
                PartyType.CUSTOMER,
                customerId,
                Sort.by(Sort.Direction.ASC, "id")
        );

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalApprovedPayment = BigDecimal.ZERO;

        for (PartyLedgerEntry entry : entries) {
            BigDecimal debit = entry.getDebitAmount() != null ? entry.getDebitAmount() : BigDecimal.ZERO;
            BigDecimal credit = entry.getCreditAmount() != null ? entry.getCreditAmount() : BigDecimal.ZERO;

            totalSales = totalSales.add(credit);
            totalApprovedPayment = totalApprovedPayment.add(debit);
        }

        return totalSales.subtract(totalApprovedPayment);
    }



    @Override
    @Transactional
    public CustomerPaymentResponseDTO createPayment(CustomerPaymentRequestDTO request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found."));

        SalesOrderHeader salesOrder = null;
        if (request.getSalesOrderId() != null) {
            salesOrder = salesOrderHeaderRepository.findById(request.getSalesOrderId())
                    .orElseThrow(() -> new RuntimeException("Sales order not found."));
        }

        Employee receiver = null;
        if (request.getReceivedByEmployeeId() != null) {
            receiver = employeeRepository.findById(request.getReceivedByEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found."));
        }

        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero.");
        }

        // approved due check (important)
        CustomerDueSummaryDTO summary = buildCustomerDueSummary(customer);
        if (request.getAmount().compareTo(summary.getCurrentDue()) > 0) {
            throw new RuntimeException("Payment amount cannot exceed current due.");
        }



        //this validation maybe done before , see above code
        BigDecimal currentDue = getCurrentCustomerDue(customer.getId());

        if (request.getAmount().compareTo(currentDue) > 0) {
            throw new RuntimeException("Payment amount cannot exceed customer current due.");
        }




        CustomerPayment payment = new CustomerPayment();
        payment.setVoucherNo(invoiceNumberService.generate("RCV"));
        payment.setCustomer(customer);
        payment.setSalesOrder(salesOrder);
        payment.setReceivedByEmployee(receiver);
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setRemarks(request.getRemarks());
        payment.setStatus(CustomerPaymentStatus.PENDING_APPROVAL);

        CustomerPayment saved = customerPaymentRepository.save(payment);
        return map(saved);
    }

    @Override
    @Transactional
    public CustomerPaymentResponseDTO approvePayment(Long paymentId, CustomerPaymentApprovalRequestDTO request) {
        CustomerPayment payment = customerPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found."));

        if (payment.getStatus() == CustomerPaymentStatus.APPROVED) {
            throw new RuntimeException("Payment already approved.");
        }
        if (payment.getStatus() == CustomerPaymentStatus.REJECTED) {
            throw new RuntimeException("Rejected payment cannot be approved.");
        }


        //use this when jwt is completed
//        Users approver = null;
//        if (request.getApprovedByUserId() != null) {
//            approver = usersRepository.findById(request.getApprovedByUserId())
//                    .orElseThrow(() -> new RuntimeException("Approver user not found."));
//        }

        Users approver = null;

        if (request.getApprovedByUserId() != null) {
            approver = usersRepository.findById(request.getApprovedByUserId()).orElse(null);

            // optional log only, fail করবে না
            if (approver == null) {
                System.out.println("Warning: Approver user not found for ID = " + request.getApprovedByUserId());
            }
        }

        payment.setStatus(CustomerPaymentStatus.APPROVED);
        payment.setApprovedBy(approver);
        payment.setApprovedDate(java.time.LocalDate.now());

        CustomerPayment saved = customerPaymentRepository.save(payment);

        // FINAL ledger impact only after approval
        ledgerService.createCustomerPaymentEntry(saved);


        // TODO: later replace with actual email + notification logic
        invoiceDeliveryService.notifySellerPaymentApproved(saved);

        return map(saved);
    }

    @Override
    @Transactional
    public CustomerPaymentResponseDTO rejectPayment(Long paymentId, CustomerPaymentApprovalRequestDTO request) {
        CustomerPayment payment = customerPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found."));

        if (payment.getStatus() == CustomerPaymentStatus.APPROVED) {
            throw new RuntimeException("Approved payment cannot be rejected.");
        }

        if (payment.getStatus() == CustomerPaymentStatus.REJECTED) {
            throw new RuntimeException("Payment already rejected.");
        }


        payment.setStatus(CustomerPaymentStatus.REJECTED);
        CustomerPayment saved = customerPaymentRepository.save(payment);
        return map(saved);


    }

    @Override
    public List<CustomerPaymentResponseDTO> getPaymentsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found."));
        return customerPaymentRepository.findByCustomer(customer).stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<CustomerPaymentResponseDTO> getPendingPayments() {
        return customerPaymentRepository.findByStatus(CustomerPaymentStatus.PENDING_APPROVAL).stream()
                .map(this::map)
                .toList();
    }

    @Override
    public CustomerDueSummaryDTO searchCustomerDueSummary(String keyword) {
        String clean = keyword == null ? "" : keyword.trim();

        if (clean.isBlank()) {
            throw new RuntimeException("Search keyword is required.");
        }

        List<Customer> customers = customerRepository
                .findByMobileNumberContainingOrNameContainingIgnoreCase(clean, clean);

        if (customers.isEmpty()) {
            throw new RuntimeException("Customer not found.");
        }

        // first match return (later autocomplete করতে পারো)
        Customer customer = customers.get(0);
        return buildCustomerDueSummary(customer);
    }



    private CustomerDueSummaryDTO buildCustomerDueSummary(Customer customer) {
        List<PartyLedgerEntry> entries = ledgerRepository.findByPartyTypeAndPartyId(
                PartyType.CUSTOMER,
                customer.getId(),
                Sort.by(Sort.Direction.ASC, "id")
        );

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalPayment = BigDecimal.ZERO;

        for (PartyLedgerEntry entry : entries) {
            BigDecimal debit = entry.getDebitAmount() != null ? entry.getDebitAmount() : BigDecimal.ZERO;
            BigDecimal credit = entry.getCreditAmount() != null ? entry.getCreditAmount() : BigDecimal.ZERO;

            totalSales = totalSales.add(credit);
            totalPayment = totalPayment.add(debit);
        }

        CustomerDueSummaryDTO dto = new CustomerDueSummaryDTO();
        dto.setCustomerId(customer.getId());
        dto.setCustomerName(customer.getName());
        dto.setMobileNumber(customer.getMobileNumber());
        dto.setCompanyName(customer.getCompanyName());
        dto.setAddress(customer.getAddress());
        dto.setTotalSales(totalSales);
        dto.setTotalApprovedPayment(totalPayment);
        dto.setCurrentDue(totalSales.subtract(totalPayment));

        customerPaymentRepository
                .findTopByCustomerAndStatusOrderByPaymentDateDescIdDesc(customer, CustomerPaymentStatus.APPROVED)
                .ifPresent(p -> dto.setLastPaymentDate(p.getPaymentDate()));

        salesOrderHeaderRepository
                .findTopByCustomerOrderBySalesDateDescIdDesc(customer)
                .ifPresent(s -> dto.setLastSaleDate(s.getSalesDate()));

        return dto;
    }



    @Override
    public List<CustomerPaymentResponseDTO> getAdminPaymentList(String keyword, String status) {
        List<CustomerPayment> payments;

        String cleanKeyword = keyword != null ? keyword.trim() : "";
        String cleanStatus = status != null ? status.trim().toUpperCase() : "ALL";

        boolean hasKeyword = !cleanKeyword.isBlank();
        boolean allStatus = cleanStatus.equals("ALL");

        if (allStatus && !hasKeyword) {
            payments = customerPaymentRepository.findAllByOrderByIdDesc();
        }
        else if (!allStatus && !hasKeyword) {
            CustomerPaymentStatus paymentStatus = CustomerPaymentStatus.valueOf(cleanStatus);
            payments = customerPaymentRepository.findByStatusOrderByIdDesc(paymentStatus);
        }
        else if (allStatus) {
            payments = searchWithoutStatus(cleanKeyword);
        }
        else {
            CustomerPaymentStatus paymentStatus = CustomerPaymentStatus.valueOf(cleanStatus);
            payments = searchWithStatus(paymentStatus, cleanKeyword);
        }

        return payments.stream().map(this::map).toList();
    }

    private List<CustomerPayment> searchWithoutStatus(String keyword) {
        List<CustomerPayment> byVoucher = customerPaymentRepository
                .findByVoucherNoContainingIgnoreCaseOrderByIdDesc(keyword);

        if (!byVoucher.isEmpty()) return byVoucher;

        List<CustomerPayment> byMobile = customerPaymentRepository
                .findByCustomer_MobileNumberContainingIgnoreCaseOrderByIdDesc(keyword);

        if (!byMobile.isEmpty()) return byMobile;

        return customerPaymentRepository
                .findByCustomer_NameContainingIgnoreCaseOrderByIdDesc(keyword);
    }

    private List<CustomerPayment> searchWithStatus(CustomerPaymentStatus status, String keyword) {
        List<CustomerPayment> byVoucher = customerPaymentRepository
                .findByStatusAndVoucherNoContainingIgnoreCaseOrderByIdDesc(status, keyword);

        if (!byVoucher.isEmpty()) return byVoucher;

        List<CustomerPayment> byMobile = customerPaymentRepository
                .findByStatusAndCustomer_MobileNumberContainingIgnoreCaseOrderByIdDesc(status, keyword);

        if (!byMobile.isEmpty()) return byMobile;

        return customerPaymentRepository
                .findByStatusAndCustomer_NameContainingIgnoreCaseOrderByIdDesc(status, keyword);
    }


    private CustomerPaymentResponseDTO map(CustomerPayment payment) {
        CustomerPaymentResponseDTO dto = new CustomerPaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setVoucherNo(payment.getVoucherNo());
        dto.setCustomerId(payment.getCustomer().getId());
        dto.setCustomerName(payment.getCustomer().getName());
        dto.setCustomerMobile(payment.getCustomer().getMobileNumber());
        dto.setSalesInvoiceNumber(payment.getSalesOrder() != null ? payment.getSalesOrder().getInvoiceNumber() : null);
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setRemarks(payment.getRemarks());
        dto.setStatus(payment.getStatus());
        return dto;
    }
}
