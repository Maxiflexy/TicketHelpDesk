package com.maxiflexy.tickethelpdeskapp.controllers;

import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.request.CompletePasswordResetRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.request.EmailRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.request.LoginRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.request.RefreshTokenRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.response.LoginResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.response.PasswordResetResponse;
import com.maxiflexy.tickethelpdeskapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/v1/ticket-helpdesk/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login (@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity
                .ok()
                .body(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken (@Validated @RequestBody RefreshTokenRequest tokenRequest) {
        return ResponseEntity
                .ok()
                .body(authService.refreshToken(tokenRequest));
    }

    @PostMapping("/initiate-reset-password")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> initiateResetPassword (@Validated @RequestBody EmailRequest emailRequest) {
        return ResponseEntity
                .ok()
                .body(authService.initiatePasswordReset(emailRequest));
    }

    @PostMapping("/complete-reset-password")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> completePasswordReset (@Validated @RequestBody CompletePasswordResetRequest resetRequest) {
        return ResponseEntity
                .ok()
                .body(authService.completePasswordReset(resetRequest));
    }
}
