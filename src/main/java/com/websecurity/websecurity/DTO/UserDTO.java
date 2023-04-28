package com.websecurity.websecurity.DTO;

import com.websecurity.websecurity.models.User;

public class UserDTO {
    public String name;
    public String surname;
    public String username;
    public String password;
    public String phone;

    boolean emailValidation;

    public User toUser(){
        return new User(name,surname,username,password);
    }

    public UserDTO() {
    }

    public UserDTO(String name, String surname, String username, String password) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isEmailValidation() {
        return emailValidation;
    }
}

