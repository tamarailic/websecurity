package com.websecurity.websecurity.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@EnableWebMvc
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    @ExceptionHandler({ ExpiredJwtException.class, AuthenticationCredentialsNotFoundException.class })
    public ResponseEntity<Object> handleException(final Exception e, final HttpServletRequest request) {
        return new ResponseEntity<>("Unauthorized!", HttpStatus.UNAUTHORIZED);
    }
}