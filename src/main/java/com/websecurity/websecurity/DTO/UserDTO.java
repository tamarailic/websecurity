package com.websecurity.websecurity.DTO;

import com.websecurity.websecurity.models.User;

public class UserDTO {
    public String name;
    public String surname;
    public String username;
    public String password;
    public String phone;
    public String recaptcha;

    boolean emailValidation;

    public UserDTO() {
    }

    public UserDTO(String name, String surname, String username, String password, String phone, String recaptcha, boolean emailValidation) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.recaptcha = recaptcha;
        this.emailValidation = emailValidation;
    }

    public UserDTO(OauthInfoDTO oauth) {
        this.name = oauth.getFullName().split(" ")[0];
        this.surname = oauth.getFullName().split(" ")[1];
        this.username = oauth.getUsername();
    }

    public UserDTO(String name, String surname, String username, String password) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
    }

    public User toUser() {
        return new User(name, surname, username, password, emailValidation, phone);
    }

    public String getRecaptcha() {
        return recaptcha;
    }

    public void setRecaptcha(String recaptcha) {
        this.recaptcha = recaptcha;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isEmailValidation() {
        return emailValidation;
    }

    public void setEmailValidation(boolean emailValidation) {
        this.emailValidation = emailValidation;
    }
}

