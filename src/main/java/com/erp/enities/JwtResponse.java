package com.erp.enities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private Users user;
    private String jwtToken;

    public JwtResponse(Users user, String jwtToken) {
        this.user = user;
        this.jwtToken = jwtToken;
    }


}
