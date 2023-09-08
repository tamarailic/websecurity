package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.models.CertificateRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ICertificateRequestRepository extends MongoRepository<CertificateRequest, String> {

    List<CertificateRequest> findAllBySubjectId(String subjectId);

    List<CertificateRequest> findAllByIssuerCertificateId(String issuerId);
}
