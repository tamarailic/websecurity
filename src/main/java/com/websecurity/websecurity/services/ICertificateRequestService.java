package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import com.websecurity.websecurity.models.Certificate;

import java.util.Collection;

public interface ICertificateRequestService {


    CertificateRequestDTO createCertificateRequestForUser(Long userId, CertificateRequestDTO certificateRequestDTO);

    CertificateRequestDTO createCertificateRequestForAdmin(Long adminId, CertificateRequestDTO certificateRequestDTO);

    Collection<CertificateRequestDTO> getAllUsersCertificateRequests(Long userId);

    Certificate approveSigningRequest(Long requestId);

    void denySigningRequest(Long requestId);

}
