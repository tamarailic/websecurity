package com.websecurity.websecurity.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.PublicKey;
import java.time.LocalDate;

@Document("certificate")
public class Certificate {

    @Id
    private String serialNumber;
    private String signingCertificateSerialNumber;
    private PublicKey publicKey;
    private CertificateOwner owner;
    private CertificateIssuer issuer;
    private Boolean endCertificate;
    private LocalDate notBefore;
    private LocalDate notAfter;
    private String version;
    private String signatureAlgorithm;
    private Boolean valid;

    public Certificate() {
    }

    public Certificate(String serialNumber, String signingCertificateSerialNumber, PublicKey publicKey, CertificateOwner owner, CertificateIssuer issuer, Boolean endCertificate, LocalDate notBefore, LocalDate notAfter, String version, String signatureAlgorithm, Boolean valid) {
        this.serialNumber = serialNumber;
        this.signingCertificateSerialNumber = signingCertificateSerialNumber;
        this.publicKey = publicKey;
        this.owner = owner;
        this.issuer = issuer;
        this.endCertificate = endCertificate;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.version = version;
        this.signatureAlgorithm = signatureAlgorithm;
        this.valid = valid;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSigningCertificateSerialNumber() {
        return signingCertificateSerialNumber;
    }

    public void setSigningCertificateSerialNumber(String signingCertificateSerialNumber) {
        this.signingCertificateSerialNumber = signingCertificateSerialNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
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
