package com.erp.dto;

import com.erp.enums.SupplierStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SupplierDTO {
    private String name;
    private String companyName;
    private String email;
    private String mobileNumber;
    private String tinNumber;
    private String address;
    private String paymentTerms;

    @Enumerated(EnumType.STRING)
    private SupplierStatus status;
    private String rating;
    private String bankAccount;
    private String bkashNo;


}
