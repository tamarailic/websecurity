package com.websecurity.websecurity.models;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("certificate_request")
public class CertificateRequest {
    @Id
    private String id;

    private String subjectId;

    private String issuerCertificateId;

    private LocalDateTime requestedDate;

    private String certificateType;

    private String status;


    public CertificateRequest() {
    }

    public CertificateRequest(String subjectId, String issuerCertificateId, LocalDateTime requestedDate, String certificateType, String status) {
        this.subjectId = subjectId;
        this.issuerCertificateId = issuerCertificateId;
        this.requestedDate = requestedDate;
        this.certificateType = certificateType;
        this.status = status;
    }

    public CertificateRequest(CertificateRequestDTO requestDTO, String subjectId, LocalDateTime requestedDate, String status) {
        this.subjectId = subjectId;
        this.issuerCertificateId = requestDTO.getIssuerCertificateId();
        this.requestedDate = requestedDate;
        this.certificateType = requestDTO.getCertificateType();
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


}
