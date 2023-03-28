package com.websecurity.websecurity.models;

public class CertificateIssuer {

    private Long issuerId;

    public Long getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(Long issuerId) {
        this.issuerId = issuerId;
    }

    public String getIssuerUsername() {
        return issuerUsername;
    }

    public void setIssuerUsername(String issuerUsername) {
        this.issuerUsername = issuerUsername;
    }

    private String issuerUsername;

}
