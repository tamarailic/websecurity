package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import com.websecurity.websecurity.DTO.CertificateRequestResponseDTO;
import com.websecurity.websecurity.models.Certificate;

import java.util.Collection;

public interface ICertificateRequestService {


    CertificateRequestResponseDTO createCertificateRequestForUser(Long userId, CertificateRequestDTO certificateRequestDTO);

    CertificateRequestResponseDTO createCertificateRequestForAdmin(Long adminId, CertificateRequestDTO certificateRequestDTO);

    Collection<CertificateRequestResponseDTO> getAllUsersCertificateRequests(Long userId);

    Certificate approveSigningRequest(Long requestId);

    void denySigningRequest(Long requestId);

}
