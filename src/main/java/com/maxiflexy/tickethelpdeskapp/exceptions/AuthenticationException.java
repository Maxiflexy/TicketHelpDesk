package com.maxiflexy.tickethelpdeskapp.exceptions;

public class AuthenticationException extends org.springframework.security.core.AuthenticationException {

    public AuthenticationException(String msg) {
        super(msg);
    }
}
