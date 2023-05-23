package com.websecurity.websecurity.models;

import com.websecurity.websecurity.security.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document("user")
public class User implements UserDetails {
    static final int PREVIOUS_PASSWORDS_COUNT = 5;
    static final int CREDENTIAL_EXPIRY_DAYS = 7;

    List<Role> roles;
    @Id
    private String id;
    private String firstName;
    private String lastName;
    @Indexed(unique = true)
    private String username;
    private String password;
    private String phone;
    private Boolean active;
    private Boolean enabled;
    private LocalDateTime credentialsExpiry;
    private Boolean nonLocked;
    private Boolean emailValidation;

    private List<String> previousPasswords;

    public User(String firstName, String lastName, String username, String password, boolean emailValidation, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.emailValidation = emailValidation;
        this.phone = phone;
        previousPasswords = new ArrayList<>();
    }

    public User() {
        previousPasswords = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return nonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsExpiry.isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCredentialsExpiry() {
        return credentialsExpiry;
    }

    public void setCredentialsExpiry(LocalDateTime credentialsExpiry) {
        this.credentialsExpiry = credentialsExpiry;
    }

    public Boolean getNonLocked() {
        return nonLocked;
    }

    public void setNonLocked(Boolean nonLocked) {
        this.nonLocked = nonLocked;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    public Boolean getEmailValidation() {
        return emailValidation;
    }

    public String getPhone() {
        return phone;
    }

    public List<String> getPreviousPasswords() {
        return previousPasswords;
    }

    public void addPreviousPassword() {
        if (previousPasswords.size() >= PREVIOUS_PASSWORDS_COUNT) {
            previousPasswords.remove(0);
        }
        previousPasswords.add(this.password);
    }

    public void refreshExpirationDate(){
        this.credentialsExpiry = LocalDateTime.now().plusDays(CREDENTIAL_EXPIRY_DAYS);
    }
}
