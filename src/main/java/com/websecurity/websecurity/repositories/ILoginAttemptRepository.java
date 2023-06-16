package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.models.CertificateRequest;
import com.websecurity.websecurity.models.LoginAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILoginAttemptRepository extends MongoRepository<LoginAttempt, String> {
    LoginAttempt findByUserEmail(String userEmail);
}
