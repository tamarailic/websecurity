package com.websecurity.websecurity.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;

@Document("login-attempt")
public class LoginAttempt {
    @Id
    private String id;
    String userEmail;
    LocalDateTime validUntil;

    byte[] auth;

    public LoginAttempt(String userEmail, byte[] auth) {
        this.userEmail = userEmail;
        this.validUntil = LocalDateTime.now().plusMinutes(5);
        this.auth = auth;
    }

    public LoginAttempt() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public byte[] getAuth() {
        return auth;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }
}
