package com.websecurity.websecurity.DTO;


public class CredentialsDTO {
    private String email;
    private String password;
    private String recaptcha;

    public CredentialsDTO() {
    }

    public CredentialsDTO(String email, String password, String recaptcha) {
        this.email = email;
        this.password = password;
        this.recaptcha = recaptcha;
    }

    public CredentialsDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getRecaptcha() {
        return recaptcha;
    }

    public void setRecaptcha(String recaptcha) {
        this.recaptcha = recaptcha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
