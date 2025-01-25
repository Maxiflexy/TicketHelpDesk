package com.maxiflexy.tickethelpdeskapp.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordResetRequest {

    @NotNull(message = "password should not be empty")
    @NotBlank(message = "password should not be empty")
    String password;
}
