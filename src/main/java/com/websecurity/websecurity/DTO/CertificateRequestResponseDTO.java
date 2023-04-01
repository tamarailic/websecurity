package com.websecurity.websecurity.DTO;

import com.websecurity.websecurity.models.CertificateRequest;

import java.time.LocalDateTime;

public class CertificateRequestResponseDTO {
    private String subjectId;

    private String issuerCertificateId;

    private LocalDateTime requestedDate;

    private String certificateType;

    private String status;

    private String denyReason;


    public CertificateRequestResponseDTO() {
    }

    public CertificateRequestResponseDTO(CertificateRequest certificateRequest) {
        this.subjectId = certificateRequest.getSubjectId();
        this.issuerCertificateId = certificateRequest.getIssuerCertificateId();
        this.requestedDate = certificateRequest.getRequestedDate();
        this.certificateType = certificateRequest.getCertificateType();
        this.status = certificateRequest.getStatus();
        this.denyReason = certificateRequest.getDenyReason();
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
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

    public String getDenyReason() {
        return denyReason;
    }

    public void setDenyReason(String denyReason) {
        this.denyReason = denyReason;
    }
}
