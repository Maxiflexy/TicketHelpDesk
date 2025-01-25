package com.maxiflexy.tickethelpdeskapp.service;

import com.infometics.helpdesk.model.OTP;

public interface OtpService {

    OTP findOtp(String otp, String email);
}
