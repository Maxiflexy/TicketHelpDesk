package com.maxiflexy.tickethelpdeskapp.repository;

import com.infometics.helpdesk.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByOtpAndOtpEmailAndOtpTimeIsAfter(String otp, String email, LocalDateTime otpTime);
}
