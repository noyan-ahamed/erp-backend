package com.erp.enities;

import com.erp.enums.SupplierStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;
    @Column(name = "company_name")
    private String companyName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(nullable = false, unique = true)
    private String mobileNumber;
    @Column(name = "tin_number")
    private String tinNumber;
    private String address;
    @Column(name = "payment_terms")
    private String paymentTerms;

    @Enumerated(EnumType.STRING)
    private SupplierStatus status;
    private String rating;
    @Column(name = "bank_account")
    private String bankAccount;
    private String bkashNo;
    @Column(name = "created_at")
    private LocalDate createdAt;

    //getters Setters

}
