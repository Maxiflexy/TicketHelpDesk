package com.maxiflexy.tickethelpdeskapp.service;

import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.request.CompletePasswordResetRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.request.EmailRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.request.LoginRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.request.RefreshTokenRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.response.LoginResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.response.PasswordResetResponse;

public interface AuthService {

    ApiResponse<LoginResponse> authenticateUser(LoginRequest loginRequest);

    ApiResponse<LoginResponse> refreshToken(RefreshTokenRequest tokenRequest);

    ApiResponse<PasswordResetResponse> initiatePasswordReset(EmailRequest emailRequest);

    ApiResponse<PasswordResetResponse> completePasswordReset(CompletePasswordResetRequest passwordResetRequest);
}
