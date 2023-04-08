package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.models.Certificate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface ICertificateRepository extends MongoRepository<Certificate, String> {
    Certificate findBySerialNumber(String serialNumber);

    Set<Certificate> findAllByOwnerId(String ownerId);
}
