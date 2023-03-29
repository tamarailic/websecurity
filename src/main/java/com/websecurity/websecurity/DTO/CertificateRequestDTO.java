package com.websecurity.websecurity.DTO;

import com.websecurity.websecurity.models.CertificateRequest;

import java.time.LocalDateTime;

public class CertificateRequestDTO {

    private Long subjectId;

    private String issuerCertificateId;

    private LocalDateTime requestedDate;

    private String certificateType;
    private String status;


    public CertificateRequestDTO() {
    }

    public CertificateRequestDTO(CertificateRequest certificateRequest) {
        this.issuerCertificateId = certificateRequest.getIssuerCertificateId();
        this.requestedDate = certificateRequest.getRequestedDate();
        this.certificateType = certificateRequest.getCertificateType();
        this.status = certificateRequest.getStatus();
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getIssuerCertificateId() {
        return issuerCertificateId;
    }

    public void setIssuerCertificateId(String issuerCertificateId) {
        this.issuerCertificateId = issuerCertificateId;
    }

    public LocalDateTime getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
