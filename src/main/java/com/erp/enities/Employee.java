package com.erp.enities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @ManyToOne
    @JoinColumn(name = "designation_id")
    private Designation designation;

//    @ManyToOne
//    @JoinColumn(name = "department_id")
//    private Department department;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "basic_salary")
    private Double basicSalary;

    @Column(name = "bank_account")
    private String bankAccount;

    private String address;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

}
