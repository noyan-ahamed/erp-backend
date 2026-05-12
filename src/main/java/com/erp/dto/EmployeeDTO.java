package com.erp.dto;

import com.erp.enities.Designation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeDTO {
    private String name;
//    private String employeeCode;
    private String email;
    private String mobileNumber;
    private Long designationId;
//    private String department;
    private LocalDate joiningDate;
    private Double basicSalary;
    private String bankAccount;
    private String address;
//    private String role;
    private LocalDate createdAt;
}
