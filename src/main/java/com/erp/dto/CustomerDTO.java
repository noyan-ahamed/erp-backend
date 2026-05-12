package com.erp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDTO {
    private String name;
    private String companyName;
    private String email;
    private String mobileNumber;
    private String address;
    private Double creditLimit;
}
