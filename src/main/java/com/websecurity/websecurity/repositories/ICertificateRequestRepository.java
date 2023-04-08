package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import com.websecurity.websecurity.DTO.CertificateRequestResponseDTO;
import com.websecurity.websecurity.models.CertificateRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ICertificateRequestRepository extends MongoRepository<CertificateRequest, String> {

    List<CertificateRequestResponseDTO> findAllBySubjectId(String subjectId);
    List<CertificateRequestResponseDTO> findAllByIssuerCertificateId(String issuerId);
}
