package com.erp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirstPasswordChangeDTO {

    private String newPassword;

    private String confirmPassword;
}