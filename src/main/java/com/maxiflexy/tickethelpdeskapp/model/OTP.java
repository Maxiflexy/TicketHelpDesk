package com.maxiflexy.tickethelpdeskapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name = "otp")
public class OTP extends BaseEntity {

    @Column(name = "otp")
    private String otp;
    @Column(name = "otp_time")
    private LocalDateTime otpTime;
    @Column(name = "otp_email")
    private String otpEmail;

}
