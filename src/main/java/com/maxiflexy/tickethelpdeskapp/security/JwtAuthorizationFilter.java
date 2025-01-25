package com.maxiflexy.tickethelpdeskapp.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infometics.helpdesk.constants.AppConstant;
import com.infometics.helpdesk.dtos.global.ApiResponse;
import com.infometics.helpdesk.utils.AppUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

import static com.infometics.helpdesk.utils.AppUtil.getPrivateKey;
import static com.infometics.helpdesk.utils.AppUtil.getPublicKey;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final AppUtil appUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper mapper;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, AppUtil appUtil, UserDetailsService userDetailsService, ObjectMapper mapper) {
        super(authenticationManager);
        this.appUtil = appUtil;
        this.userDetailsService = userDetailsService;
        this.mapper = mapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (Objects.isNull(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        var unauthorizedResponse = ApiResponse.builder().status(false)
                .responseCode(AppConstant.failedResponseCode)
                .responseMessage("Invalid token")
                .build();
        response.getWriter().write(mapper.writeValueAsString(unauthorizedResponse));
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
            String email = null;

            try {

                RSAPublicKey publickey = (RSAPublicKey) getPublicKey(appUtil.getPublicKey());
                RSAPrivateKey privateKey = (RSAPrivateKey) getPrivateKey(appUtil.getPrivKey());

                JWTVerifier jwtV = JWT.require(Algorithm.RSA256(publickey, privateKey)).build();
                DecodedJWT jwtD = jwtV.verify(token.replace("Bearer ", ""));
                email = jwtD.getClaim("email").asString();
                UserDetails user = userDetailsService.loadUserByUsername(email);
                if (user.isAccountNonExpired() && user.isEnabled() && user.isAccountNonLocked()) {
                    return new UsernamePasswordAuthenticationToken(email, null, user.getAuthorities());
                }
            } catch (UsernameNotFoundException e) {
                log.warn("Some exception : {} failed : {}", token, e.getMessage());
            } catch (JWTVerificationException e) {
                log.warn("Some exception : {} failed : {}", token, e.getMessage());
            }
        }
        return null;
//        throw new BadCredentialsException("Invalid authentication");
    }
}

