package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.models.Certificate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.awt.print.Pageable;

public interface ICertificateRepository extends MongoRepository<Certificate, String> {
    Certificate findBySerialNumber(String serialNumber);
}
