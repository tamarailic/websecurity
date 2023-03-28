package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.models.Certificate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICertificateRepository extends MongoRepository<Certificate, String> {
}
