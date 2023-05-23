package com.websecurity.websecurity.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("login-attempt")
public class LoginAttempt {
    @Id
    private String id;
    User user;
    String code;

    LocalDateTime validUntil;

    public LoginAttempt(User user, String code, LocalDateTime validUntil) {
        this.user = user;
        this.code = code;
        this.validUntil = validUntil;
    }

    public LoginAttempt() {
    }

    public User getUser() {
        return user;
    }

    public String getCode() {
        return code;
    }
}
