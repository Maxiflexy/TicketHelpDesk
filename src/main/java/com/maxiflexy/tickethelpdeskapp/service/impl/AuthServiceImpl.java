package com.maxiflexy.tickethelpdeskapp.service.impl;

import com.infometics.helpdesk.constants.AppConstant;
import com.infometics.helpdesk.dtos.global.ApiResponse;
import com.infometics.helpdesk.dtos.request.*;
import com.infometics.helpdesk.dtos.response.LoginResponse;
import com.infometics.helpdesk.dtos.response.PasswordResetResponse;
import com.infometics.helpdesk.model.MailModel;
import com.infometics.helpdesk.model.OTP;
import com.infometics.helpdesk.model.User;
import com.infometics.helpdesk.repository.OtpRepository;
import com.infometics.helpdesk.repository.RoleRepository;
import com.infometics.helpdesk.repository.UserRepository;
import com.infometics.helpdesk.service.AuthService;
import com.infometics.helpdesk.service.OtpService;
import com.infometics.helpdesk.utils.AppUtil;
import com.infometics.helpdesk.utils.BaseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthServiceImpl extends UserHandler implements AuthService {


    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AppUtil appUtil;
    private final OtpRepository otpRepository;
    private final EmailSenderService emailSenderService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, AppUtil appUtil, OtpRepository otpRepository, EmailSenderService emailSenderService, OtpService otpService, PasswordEncoder passwordEncoder) {
        super(userRepository, roleRepository);
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.appUtil = appUtil;
        this.otpRepository = otpRepository;
        this.emailSenderService = emailSenderService;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiResponse<LoginResponse> authenticateUser(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            log.error("Incorrect username or password");
            throw badCredentialsException;
        }
        User user = fetchUserByEmail(loginRequest.getEmail());
        return getLoginApiResponse(user);
    }

    private ApiResponse<LoginResponse> getLoginApiResponse(User user) {
        BaseBean token = appUtil.generateToken(user.getEmail(), user.getRole().getRoleName());
        user.setRefreshToken(token.getString("token-id"));
        userRepository.save(user);
        return ApiResponse.<LoginResponse>builder()
                .responseCode(user.isFirstLogin()? AppConstant.firstTImeLogin : AppConstant.successResponseCode)
                .responseMessage(user.isFirstLogin() ? "Authentication successful. Reset Default password" : "Authentication successful")
                .status(true)
                .data(LoginResponse.builder()
                        .token(token.getString("jwt"))
                        .refreshToken(token.getString("refresh-token"))
                        .roleName(user.getRole().getRoleName())
                        .email(user.getEmail())
                        .orgName(user.getOrganization().getOrgName())
                        .tokenExpirationTime(token.getString("token-expiration"))
                        .build())
                .build();
    }

    @Override
    public ApiResponse<LoginResponse> refreshToken(RefreshTokenRequest tokenRequest) {
        User user = fetchUserByEmail(tokenRequest.getEmail());
        BaseBean tokenBean = new BaseBean();
        if (!appUtil.verifyToken(tokenRequest.getRefreshToken(), tokenBean)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        if (!user.getRefreshToken().equals(tokenBean.getString("refresh-id"))) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        return getLoginApiResponse(user);
    }

    @Override
    public ApiResponse<PasswordResetResponse> initiatePasswordReset(EmailRequest emailRequest) {
        User user = fetchUserByEmail(emailRequest.getEmail());
        String token = AppUtil.createRandomInteger(6);
        OTP otp = new OTP();
        otp.setOtp(token);
        otp.setOtpEmail(emailRequest.getEmail());
        otp.setOtpTime(LocalDateTime.now().plusMinutes(15));
        otpRepository.save(otp);
        sendOTPMessage(user, token);
        return ApiResponse.<PasswordResetResponse>builder()
                .status(true)
                .responseCode(AppConstant.successResponseCode)
                .responseMessage("OTP sent")
                .build();
    }

    @Override
    public ApiResponse<PasswordResetResponse> completePasswordReset(CompletePasswordResetRequest passwordResetRequest) {
        User user = fetchUserByEmail(passwordResetRequest.getEmail());
        if (otpService.findOtp(passwordResetRequest.getOtp(), passwordResetRequest.getEmail()).getOtp().equals(passwordResetRequest.getOtp())) {
            String newPassword = AppUtil.createRandomString(AppConstant.passwordLength);
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setFirstLogin(true);
            userRepository.save(user);
            sendPasswordResetMessage(user, newPassword);
            return ApiResponse.<PasswordResetResponse>builder()
                    .status(true)
                    .responseCode(AppConstant.successResponseCode)
                    .responseMessage("Password reset success, check mail for new password")
                    .build();
        }

        return ApiResponse.<PasswordResetResponse>builder()
                .status(false)
                .responseCode(AppConstant.successResponseCode)
                .responseMessage("Password reset failed, Invalid OTP")
                .build();

    }

    private void sendPasswordResetMessage(User user, String newPassword) {
        MailModel model = new MailModel();
        try {

            log.info("Sending email");
            model.setFrom("michael.afolabi@infometics.net");
            model.setSubject("TICKET HELPDESK");
            model.setUseTemplate(true);
            model.setTemplateName("password_notification.tpl");

            Map<String, String> mMap = new HashMap<>();
            mMap.put("initiator", user.getFirstName());
            mMap.put("password", newPassword);
            mMap.put("email", user.getEmail());

            model.setMessageMap(mMap);
            model.setTo(new String[]{user.getEmail()});
            emailSenderService.sendEmail(model);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void sendOTPMessage(User user,String message) {
        MailModel model = new MailModel();
        try {

            log.info("Sending email");
            model.setFrom("michael.afolabi@infometics.net");
            model.setSubject("TICKET HELPDESK");
            model.setUseTemplate(true);
            model.setTemplateName("otp_notification.tpl");

            Map<String, String> mMap = new HashMap<>();
            mMap.put("initiator", user.getFirstName());
            mMap.put("OTP", message);
            mMap.put("email", user.getEmail());

            model.setMessageMap(mMap);
            model.setTo(new String[]{user.getEmail()});
            emailSenderService.sendEmail(model);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
