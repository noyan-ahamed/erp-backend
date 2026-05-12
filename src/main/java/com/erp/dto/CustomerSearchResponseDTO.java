package com.erp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSearchResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String companyName;
    private String mobileNumber;
    private String address;
}
