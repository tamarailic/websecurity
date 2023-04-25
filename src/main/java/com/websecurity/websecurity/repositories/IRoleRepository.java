package com.websecurity.websecurity.repositories;

import com.websecurity.websecurity.security.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IRoleRepository extends MongoRepository<Role, String> {
    List<Role> findByName(String name);
}
