package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface IUserRepository extends MongoRepository<User, Long> {
    User findByUsername(String username);

}
