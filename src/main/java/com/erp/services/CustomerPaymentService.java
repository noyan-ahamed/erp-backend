package com.erp.services;

import com.erp.dto.CustomerDueSummaryDTO;
import com.erp.dto.CustomerPaymentApprovalRequestDTO;
import com.erp.dto.CustomerPaymentRequestDTO;
import com.erp.dto.CustomerPaymentResponseDTO;

import java.util.List;

public interface CustomerPaymentService {
    CustomerPaymentResponseDTO createPayment(CustomerPaymentRequestDTO request);
    CustomerPaymentResponseDTO approvePayment(Long paymentId, CustomerPaymentApprovalRequestDTO request);
    CustomerPaymentResponseDTO rejectPayment(Long paymentId, CustomerPaymentApprovalRequestDTO request);
    List<CustomerPaymentResponseDTO> getPaymentsByCustomer(Long customerId);
    List<CustomerPaymentResponseDTO> getPendingPayments();


    CustomerDueSummaryDTO searchCustomerDueSummary(String keyword);

    // NEW
    List<CustomerPaymentResponseDTO> getAdminPaymentList(String keyword, String status);
}
