package com.example.Blogera_demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.Blogera_demo.model.Like;


@Repository
public interface LikeRepository extends MongoRepository<Like,String>{
      // Find likes by the postId
    List<Like> findByPostId(String postId);

    // Find likes by the userId
    List<Like> findByUserId(String userId);

    long countByPostId(String postId);

    boolean existsByUserIdAndPostId(String userId, String postId);

    void deleteByUserIdAndPostId(String userId, String postId);
}
