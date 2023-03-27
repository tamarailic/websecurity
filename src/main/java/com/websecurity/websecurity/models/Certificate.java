package com.websecurity.websecurity.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.PublicKey;
import java.time.LocalDate;

@Document("certificate")
public class Certificate {

    @Id
    private Long serialNumber;
    private PublicKey publicKey;
    private CertificateOwner owner;
    private CertificateIssuer issuer;
    private Boolean endCertificate;
    private LocalDate notBefore;
    private LocalDate notAfter;
    private Long version;
    private String signatureAlgorithm;
    private Boolean valid;

    public Certificate() {
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public CertificateOwner getOwner() {
        return owner;
    }

    public void setOwner(CertificateOwner owner) {
        this.owner = owner;
    }

    public CertificateIssuer getIssuer() {
        return issuer;
    }

    public void setIssuer(CertificateIssuer issuer) {
        this.issuer = issuer;
    }

    public Boolean getEndCertificate() {
        return endCertificate;
    }

    public void setEndCertificate(Boolean endCertificate) {
        this.endCertificate = endCertificate;
    }

    public LocalDate getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(LocalDate notBefore) {
        this.notBefore = notBefore;
    }

    public LocalDate getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(LocalDate notAfter) {
        this.notAfter = notAfter;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
