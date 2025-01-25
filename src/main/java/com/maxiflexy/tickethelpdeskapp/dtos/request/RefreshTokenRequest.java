package com.maxiflexy.tickethelpdeskapp.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotNull(message = "email should not be empty")
    @NotBlank(message = "email should not be empty")
    String email;
    @NotNull(message = "refreshToken should not be empty")
    @NotBlank(message = "refreshToken should not be empty")
    String refreshToken;

}
