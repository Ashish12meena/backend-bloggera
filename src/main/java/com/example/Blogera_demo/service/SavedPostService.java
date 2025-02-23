package com.example.Blogera_demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.Blogera_demo.dto.SavedPostDto;
import com.example.Blogera_demo.model.SavedPost;
import com.example.Blogera_demo.repository.SavedPostRepository;

@Service
public class SavedPostService {

    @Autowired
    private SavedPostRepository savedPostRepository;

    public ResponseEntity<SavedPostDto> addSavedPost(String userId, String postId) {
        SavedPost savedPost = new SavedPost();
        savedPost.setUserId(userId);
        savedPost.setPostId(postId);
        SavedPost saved = savedPostRepository.save(savedPost);

        SavedPostDto dto = new SavedPostDto(saved.getUserId(), saved.getPostId());
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<List<SavedPostDto>> getSavedPost(String userId) {
        List<SavedPost> listOfSavedPosts = savedPostRepository.findByUserId(userId);

        List<SavedPostDto> dtos = listOfSavedPosts.stream()
                .map(savedPost -> new SavedPostDto(savedPost.getUserId(), savedPost.getPostId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<?> removeSavedPost(String userId, String postId) {
        savedPostRepository.deleteByUserIdAndPostId(userId, postId);
        return ResponseEntity.ok("Removed successfully");
    }
}
