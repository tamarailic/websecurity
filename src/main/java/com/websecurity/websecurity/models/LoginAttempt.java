package com.websecurity.websecurity.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("login-attempt")
public class LoginAttempt {
    String userEmail;
    String code;
    LocalDateTime validUntil;
    @Id
    private String id;


    public LoginAttempt(String userEmail, String code) {
        this.userEmail = userEmail;
        this.code = code;
        this.validUntil = LocalDateTime.now().plusMinutes(5);
    }

    public LoginAttempt() {
    }

    public String getCode() {
        return code;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }
}
