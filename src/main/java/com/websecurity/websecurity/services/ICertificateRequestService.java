package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import com.websecurity.websecurity.DTO.CertificateRequestResponseDTO;
import com.websecurity.websecurity.DTO.ReasonDTO;
import com.websecurity.websecurity.models.Certificate;

import java.util.Collection;

public interface ICertificateRequestService {


    CertificateRequestResponseDTO createCertificateRequestForUser(String userId, CertificateRequestDTO certificateRequestDTO);

    CertificateRequestResponseDTO createCertificateRequestForAdmin(String adminId, CertificateRequestDTO certificateRequestDTO);

    Collection<CertificateRequestResponseDTO> getAllUsersCertificateRequests(String userId);

    Certificate approveSigningRequest(String requestId);

    void denySigningRequest(String requestId, ReasonDTO denyReason);

}
