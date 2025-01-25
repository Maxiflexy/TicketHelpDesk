package com.maxiflexy.tickethelpdeskapp.service.impl;

import com.infometics.helpdesk.exceptions.BadRequestException;
import com.infometics.helpdesk.model.OTP;
import com.infometics.helpdesk.repository.OtpRepository;
import com.infometics.helpdesk.service.OtpService;
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
