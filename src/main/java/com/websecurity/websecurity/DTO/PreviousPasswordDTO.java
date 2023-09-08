package com.websecurity.websecurity.DTO;

public class PreviousPasswordDTO {

    private String oldPassword;
    private String password;
    private String username;

    public PreviousPasswordDTO() {
    }

    public PreviousPasswordDTO(String oldPassword, String password, String username) {
        this.oldPassword = oldPassword;
        this.password = password;
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
