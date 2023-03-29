package com.websecurity.websecurity.models;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("certificate_request")
public class CertificateRequest {
    @Id
    private Long id;

    private Long subjectId;

    private String issuerCertificateId;

    private LocalDateTime requestedDate;

    private String certificateType;
    private String status;


    public CertificateRequest() {
    }

    public CertificateRequest(CertificateRequestDTO certificateRequestDTO) {
        this.subjectId = certificateRequestDTO.getSubjectId();
        this.issuerCertificateId = certificateRequestDTO.getIssuerCertificateId();;
        this.requestedDate = certificateRequestDTO.getRequestedDate();;
        this.certificateType = certificateRequestDTO.getCertificateType();;
        this.status = certificateRequestDTO.getStatus();;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
