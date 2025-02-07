package com.example.Blogera_demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.Blogera_demo.model.User;

@Repository
public interface AuthRepository extends MongoRepository<User, String> {

    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByEmail(String email);
    // Custom query methods can be added here
}
