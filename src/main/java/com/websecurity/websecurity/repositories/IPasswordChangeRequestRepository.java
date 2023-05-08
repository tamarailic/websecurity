package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.models.CertificateRequest;
import com.websecurity.websecurity.models.PasswordChangeRequest;
import com.websecurity.websecurity.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPasswordChangeRequestRepository extends MongoRepository<PasswordChangeRequest, String> {

    PasswordChangeRequest findByUser(User user);

    PasswordChangeRequest findByCode(String code);
}
