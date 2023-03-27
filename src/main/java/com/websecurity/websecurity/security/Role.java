package com.websecurity.websecurity.security;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

@Document("user")
public class Role implements GrantedAuthority {
    @Id
    Long id;
    String name;

    public String getName() {
        return name;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
