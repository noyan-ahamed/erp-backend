package com.erp.controllers;

import com.erp.services.reportService.ReportService;
import jakarta.mail.internet.ContentDisposition;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    @GetMapping("/purchase-report")
    public ResponseEntity<byte[]> purchaseReport(@RequestParam BigDecimal orderId) {
        Map<String,Object> params = new HashMap<>();
        params.put("ORDER_ID",orderId);
        try {
            byte[] pdf = reportService.generateReport("purchase_report", params);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=purchase_invoice.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

