package com.websecurity.websecurity.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("PasswordChangeRequest")
public class PasswordChangeRequest {
    User user;
    String code;
    @Id
    private String id;

    public PasswordChangeRequest(User user, String code) {
        this.user = user;
        this.code = code;
    }

    public PasswordChangeRequest() {
    }

    public User getUser() {
        return user;
    }

    public String getCode() {
        return code;
    }
}
