package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IUserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);

    Optional<User> findUserByUsername(String username);
}
