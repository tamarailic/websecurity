package com.websecurity.websecurity.DTO;

import com.websecurity.websecurity.models.Certificate;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class CertificateToShowDTO {

    private String serialNumber;
    private String signingCertificateSerialNumber;
    private String publicKey;
    private String owner;
    private String issuer;
    private String type;
    private String notBefore;
    private String notAfter;
    private String version;
    private String signatureAlgorithm;
    private Boolean valid;

    public CertificateToShowDTO() {
    }

    public CertificateToShowDTO(Certificate certificate) {
        String certType;
        if (certificate.isEndCertificate()) {
            certType = "END";
        } else if (Objects.equals(certificate.getSerialNumber(), certificate.getSigningCertificateSerialNumber())) {
            certType = "ROOT";
        } else {
            certType = "INTERMEDIATE";
        }

        this.serialNumber = certificate.getSerialNumber();
        this.signingCertificateSerialNumber = certificate.getSigningCertificateSerialNumber();
        this.publicKey = certificate.getPublicKey();
        this.owner = certificate.getOwner().getUsername();
        this.issuer = certificate.getIssuer().getUsername();
        this.type = certType;
        this.notBefore = certificate.getNotBefore().format(DateTimeFormatter.ofPattern("dd LLLL yyyy"));
        this.notAfter = certificate.getNotAfter().format(DateTimeFormatter.ofPattern("dd LLLL yyyy"));
        this.version = certificate.getVersion();
        this.signatureAlgorithm = certificate.getSignatureAlgorithm();
        this.valid = certificate.getValid();
    }

    public CertificateToShowDTO(String serialNumber, String signingCertificateSerialNumber, String publicKey, String owner, String issuer, String type, String notBefore, String notAfter, String version, String signatureAlgorithm, Boolean valid) {
        this.serialNumber = serialNumber;
        this.signingCertificateSerialNumber = signingCertificateSerialNumber;
        this.publicKey = publicKey;
        this.owner = owner;
        this.issuer = issuer;
        this.type = type;
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }

    public String getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
