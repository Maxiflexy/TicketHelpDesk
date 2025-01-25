package com.maxiflexy.tickethelpdeskapp.dtos.request;

import lombok.Data;

@Data
public class CompletePasswordResetRequest {

    private String email;
    private String otp;
}
