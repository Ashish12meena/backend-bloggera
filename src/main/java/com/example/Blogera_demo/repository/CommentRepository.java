package com.example.Blogera_demo.repository;

import com.example.Blogera_demo.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByUserId(String userId);
    List<Comment> findByPostId(String postId);
    long countByPostId(String postId);
}