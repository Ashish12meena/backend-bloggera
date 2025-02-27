package com.example.Blogera_demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.Blogera_demo.dto.SavedPostDto;
import com.example.Blogera_demo.model.SavedPost;
import com.example.Blogera_demo.repository.SavedPostRepository;
import com.example.Blogera_demo.serviceInterface.SavedPostServiceInterface;

@Service
public class SavedPostService implements SavedPostServiceInterface {

    @Autowired
    private SavedPostRepository savedPostRepository;

    @Override
    public ResponseEntity<SavedPostDto> addSavedPost(String userId, String postId) {
        SavedPost savedPost = new SavedPost();
        savedPost.setUserId(userId);
        savedPost.setPostId(postId);
        SavedPost saved = savedPostRepository.save(savedPost);

        SavedPostDto dto = new SavedPostDto(saved.getUserId(), saved.getPostId());
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<List<SavedPostDto>> getSavedPost(String userId) {
        List<SavedPost> listOfSavedPosts = savedPostRepository.findByUserId(userId);

        List<SavedPostDto> dtos = listOfSavedPosts.stream()
                .map(savedPost -> new SavedPostDto(savedPost.getUserId(), savedPost.getPostId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<?> removeSavedPost(String userId, String postId) {
        savedPostRepository.deleteByUserIdAndPostId(userId, postId);
        return ResponseEntity.ok("Removed successfully");
    }
}
