package com.example.Blogera_demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Blogera_demo.model.SavedPost;

public interface SavedPostRepository  extends MongoRepository<SavedPost, String>{

    List<SavedPost> findByUserId(String userId);

    void deleteByUserIdAndPostId(String userId, String postId);
    
}
