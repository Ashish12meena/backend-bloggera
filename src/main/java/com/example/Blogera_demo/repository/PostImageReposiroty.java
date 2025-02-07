package com.example.Blogera_demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Blogera_demo.model.PostImage;

public interface PostImageReposiroty extends MongoRepository<PostImage, String> {
    List<PostImage> findByPostId(String postId);
}
    