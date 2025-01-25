package com.maxiflexy.tickethelpdeskapp.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {

    @NotNull(message = "email should not be empty")
    @NotBlank(message = "email should not be empty")
    private String email;
    @NotNull(message = "password should not be empty")
    @NotBlank(message = "password should not be empty")
    private String password;
}
