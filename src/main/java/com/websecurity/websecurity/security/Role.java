package com.websecurity.websecurity.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

@Document("role")
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority {
    @Id
    String id;
    String name;

    public String getName() {
        return name;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    public String getId() {
        return id;
    }
}
