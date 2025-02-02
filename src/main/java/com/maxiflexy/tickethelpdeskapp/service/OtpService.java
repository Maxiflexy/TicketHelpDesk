package com.maxiflexy.tickethelpdeskapp.service;

import com.maxiflexy.tickethelpdeskapp.model.OTP;

public interface OtpService {

    OTP findOtp(String otp, String email);
}
