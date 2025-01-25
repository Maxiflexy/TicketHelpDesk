package com.maxiflexy.tickethelpdeskapp.service;

import com.infometics.helpdesk.dtos.global.ApiResponse;
import com.infometics.helpdesk.dtos.request.CompletePasswordResetRequest;
import com.infometics.helpdesk.dtos.request.EmailRequest;
import com.infometics.helpdesk.dtos.request.LoginRequest;
import com.infometics.helpdesk.dtos.request.RefreshTokenRequest;
import com.infometics.helpdesk.dtos.response.LoginResponse;
import com.infometics.helpdesk.dtos.response.PasswordResetResponse;

public interface AuthService {

    ApiResponse<LoginResponse> authenticateUser(LoginRequest loginRequest);

    ApiResponse<LoginResponse> refreshToken(RefreshTokenRequest tokenRequest);

    ApiResponse<PasswordResetResponse> initiatePasswordReset(EmailRequest emailRequest);

    ApiResponse<PasswordResetResponse> completePasswordReset(CompletePasswordResetRequest passwordResetRequest);
}
