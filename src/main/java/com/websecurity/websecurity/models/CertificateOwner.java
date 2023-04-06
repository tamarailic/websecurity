package com.websecurity.websecurity.models;

public class CertificateOwner {

    private String id;
    private String username;
    private String firstName;
    private String lastName;

    public CertificateOwner() {
    }

    public CertificateOwner(String Id, String username, String firstName, String lastName) {
        this.id = Id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
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
}
