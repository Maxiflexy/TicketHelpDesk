package com.maxiflexy.tickethelpdeskapp.exceptions;

import org.springframework.security.web.firewall.RequestRejectedException;

public class TokenValidationException extends RequestRejectedException {

    public TokenValidationException(String message) {
        super(message);
    }
}
