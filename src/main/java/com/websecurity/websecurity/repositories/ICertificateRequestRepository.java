package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.models.CertificateRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICertificateRequestRepository extends MongoRepository<CertificateRequest, Long> {
}
