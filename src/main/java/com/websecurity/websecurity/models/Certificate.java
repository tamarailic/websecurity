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

    private Long version;

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
}
