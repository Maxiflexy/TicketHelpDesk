package com.maxiflexy.tickethelpdeskapp.service.impl;

import com.maxiflexy.tickethelpdeskapp.exceptions.BadRequestException;
import com.maxiflexy.tickethelpdeskapp.model.OTP;
import com.maxiflexy.tickethelpdeskapp.repository.OtpRepository;
import com.maxiflexy.tickethelpdeskapp.service.OtpService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;

    public OtpServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }


    @Override
    public OTP findOtp(String otp, String email) {
        Optional<OTP> OTP = otpRepository.findByOtpAndOtpEmailAndOtpTimeIsAfter(otp, email, LocalDateTime.now());
        if (OTP.isPresent()) {
            return OTP.get();
        }
        throw new BadRequestException("Invalid OTP");
    }
}
