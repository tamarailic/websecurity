package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IUserRepository extends MongoRepository<User, Long> {
}
