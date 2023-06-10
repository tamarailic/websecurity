package com.websecurity.websecurity.DTO;

public class OauthInfoDTO {

    private String username;
    private String fullName;

    public OauthInfoDTO() {
    }

    public OauthInfoDTO(String username, String fullName) {
        this.username = username;
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
