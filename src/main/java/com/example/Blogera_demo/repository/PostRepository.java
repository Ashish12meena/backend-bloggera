package com.example.Blogera_demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Blogera_demo.model.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    List<Post> findByUserId(String email);

     @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    List<Post> searchByText(String text);

}
