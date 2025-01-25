package com.maxiflexy.tickethelpdeskapp.dtos.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token;
    private String refreshToken;
    @JsonAlias("role_name")
    private String roleName;
    private String email;
    private String orgName;
    private String tokenExpirationTime;

}
