package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import com.websecurity.websecurity.DTO.CertificateRequestResponseDTO;
import com.websecurity.websecurity.DTO.CertificateToShowDTO;
import com.websecurity.websecurity.DTO.ReasonDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface ICertificateRequestService {


    CertificateRequestResponseDTO createCertificateRequestForUser(String userId, CertificateRequestDTO certificateRequestDTO);

    CertificateRequestResponseDTO createCertificateRequestForAdmin(String adminId, CertificateRequestDTO certificateRequestDTO);

    Collection<CertificateRequestResponseDTO> getAllUsersCertificateRequests(String userId);

    Collection<CertificateRequestResponseDTO> getAllCertificateRequests();

    Collection<CertificateRequestResponseDTO> getAllUsersCertificateRequestsToReview(String userId);

    CertificateToShowDTO approveSigningRequest(String requestId);

    void denySigningRequest(String requestId, ReasonDTO denyReason);

    Page<CertificateToShowDTO> getAll(Pageable pageable);

    CertificateToShowDTO withdrawCertificateById(String certificateSerialNumber, ReasonDTO reason);

}
