package com.websecurity.websecurity.DTO;

public class CertificateRequestDTO {

    private String issuerCertificateId;

    private String certificateType;

    public CertificateRequestDTO() {
    }


    public String getIssuerCertificateId() {
        return issuerCertificateId;
    }

    public void setIssuerCertificateId(String issuerCertificateId) {
        this.issuerCertificateId = issuerCertificateId;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

}
