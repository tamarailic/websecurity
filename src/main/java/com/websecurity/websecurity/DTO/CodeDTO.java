package com.websecurity.websecurity.DTO;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("login-attempt")
public class CodeDTO {
    @Id
    String id;

    String code;

    @Indexed(unique = true)
    String email;

    String password;

    public String getPassword() {
        return password;
    }

    public String getCode() {
        return code;
    }

    public String getEmail() {
        return email;
    }
}
