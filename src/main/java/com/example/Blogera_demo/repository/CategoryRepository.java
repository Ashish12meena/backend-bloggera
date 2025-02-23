package com.example.Blogera_demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.Blogera_demo.model.Category;


@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    
}
