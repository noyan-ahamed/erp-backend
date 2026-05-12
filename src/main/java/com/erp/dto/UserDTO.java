package com.erp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDTO {

    private String name;
    private String username;
    private String email;
    private String imageBase64;
    private String imageType;
    private String status;
    private String createdAt;
}