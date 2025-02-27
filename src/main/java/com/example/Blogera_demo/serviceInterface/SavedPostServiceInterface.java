package com.example.Blogera_demo.serviceInterface;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.Blogera_demo.dto.SavedPostDto;

public interface SavedPostServiceInterface {
     ResponseEntity<SavedPostDto> addSavedPost(String userId, String postId);
     ResponseEntity<List<SavedPostDto>> getSavedPost(String userId);
     ResponseEntity<?> removeSavedPost(String userId, String postId);
}
