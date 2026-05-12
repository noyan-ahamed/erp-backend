package com.erp.controllers;

import com.erp.dto.CustomerDueSummaryDTO;
import com.erp.dto.CustomerPaymentApprovalRequestDTO;
import com.erp.dto.CustomerPaymentRequestDTO;
import com.erp.dto.CustomerPaymentResponseDTO;
import com.erp.services.CustomerPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-payments")
@RequiredArgsConstructor
public class CustomerPaymentController {

    private final CustomerPaymentService customerPaymentService;


    // =========================
    // Employee Search Summary
    // =========================
    @GetMapping("/summary")
    public CustomerDueSummaryDTO getCustomerDueSummary(@RequestParam String keyword) {
        return customerPaymentService.searchCustomerDueSummary(keyword);
    }

    // =========================
    // Employee Create Payment
    // =========================
    @PostMapping
    public CustomerPaymentResponseDTO createPayment(@RequestBody CustomerPaymentRequestDTO request) {
        return customerPaymentService.createPayment(request);
    }


    // =========================
    // Admin Approval
    // =========================
    @PutMapping("/{id}/approve")
    public CustomerPaymentResponseDTO approvePayment(
            @PathVariable Long id,
            @RequestBody CustomerPaymentApprovalRequestDTO request
    ) {
        return customerPaymentService.approvePayment(id, request);
    }


//    @PutMapping("/{id}/approve")
//    public CustomerPaymentResponseDTO approvePayment(
//            @PathVariable Long id,
//            @RequestBody CustomerPaymentApprovalRequestDTO request
//    ) {
//        return customerPaymentService.approvePayment(id, request);
//    }

//    @PutMapping("/{id}/reject")
//    public CustomerPaymentResponseDTO rejectPayment(
//            @PathVariable Long id,
//            @RequestBody CustomerPaymentApprovalRequestDTO request
//    ) {
//        return customerPaymentService.rejectPayment(id, request);
//    }

//    @GetMapping("/customer/{customerId}")
//    public List<CustomerPaymentResponseDTO> getPaymentsByCustomer(@PathVariable Long customerId) {
//        return customerPaymentService.getPaymentsByCustomer(customerId);
//    }

    @PutMapping("/{id}/reject")
    public CustomerPaymentResponseDTO rejectPayment(
            @PathVariable Long id,
            @RequestBody CustomerPaymentApprovalRequestDTO request
    ) {
        return customerPaymentService.rejectPayment(id, request);
    }

    // =========================
    // History
    // =========================
    @GetMapping("/customer/{customerId}")
    public List<CustomerPaymentResponseDTO> getPaymentsByCustomer(@PathVariable Long customerId) {
        return customerPaymentService.getPaymentsByCustomer(customerId);
    }

    // =========================
    // Admin Pending List
    // =========================
    @GetMapping("/pending")
    public List<CustomerPaymentResponseDTO> getPendingPayments() {
        return customerPaymentService.getPendingPayments();
    }

//    @GetMapping("/pending")
//    public List<CustomerPaymentResponseDTO> getPendingPayments() {
//        return customerPaymentService.getPendingPayments();
//    }


    @GetMapping("/admin/list")
    public List<CustomerPaymentResponseDTO> getAdminPaymentList(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "ALL") String status
    ) {
        return customerPaymentService.getAdminPaymentList(keyword, status);
    }
}
