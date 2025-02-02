package com.maxiflexy.tickethelpdeskapp.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.maxiflexy.tickethelpdeskapp.constants.AppConstant.failedResponseCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomError<?> handleNotFoundException(NotFoundException ex) {
        return new CustomError<>(failedResponseCode, ex.getMessage(), false);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomError<?> handleBadRequestException(BadRequestException ex) {
        return new CustomError<>(failedResponseCode, ex.getMessage(), false);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CustomError<?> handleBadCredentialsException(BadCredentialsException ex) {
        log.error(ex.getMessage(), ex);
        return new CustomError<>(failedResponseCode, "Invalid username or password", false);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CustomError<?> handleAuthenticationException(AuthenticationException ex) {
        log.error(ex.getMessage(), ex);
        return new CustomError<>(failedResponseCode, "Invalid username or password", false);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CustomError<?> handleUserNotFoundException(UsernameNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new CustomError<>(failedResponseCode, "Invalid username or password", false);
    }

    @ExceptionHandler(TokenValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CustomError<?> handleTokenValidationException(TokenValidationException ex) {
        log.error(ex.getMessage(), ex);
        return new CustomError<>(failedResponseCode, "Error validating user", false);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CustomError<?> handleDuplicateResourceException(DuplicateResourceException ex) {
        log.error(ex.getMessage(), ex);
        return new CustomError<>(failedResponseCode, ex.getMessage(), false);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomError<?> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new CustomError<>(failedResponseCode, ex.getMessage(), false);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CustomError<?> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new CustomError<>(failedResponseCode, ex.getMessage(), false, errors);

    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CustomError<?> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error(ex.getMessage(), ex);
        return new CustomError<>(failedResponseCode, ex.getMessage(), false);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomError<?> handleTicketNotFoundException(TicketNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new CustomError<>(failedResponseCode, ex.getMessage(), false);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomError<?> handleUnAuthorizedException(UnauthorizedException ex) {
        log.error(ex.getMessage(), ex);
        return new CustomError<>(failedResponseCode, ex.getMessage(), false);
    }
}
