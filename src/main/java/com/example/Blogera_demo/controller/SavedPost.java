package com.example.Blogera_demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.Blogera_demo.dto.SavedPostDto;

import com.example.Blogera_demo.service.SavedPostService;

@CrossOrigin(origins = "*", allowedHeaders = "Authorization")
@RestController
@RequestMapping("/api/saved")
public class SavedPost {
    @Autowired
    private SavedPostService savedPostService;

    @PostMapping("/add")
     public ResponseEntity<SavedPostDto> addSavedPost(@RequestBody SavedPostDto savedPostDto){
       return savedPostService.addSavedPost(savedPostDto.getUserId(), savedPostDto.getPostId());
    }
    @PostMapping("/remove")
     public ResponseEntity<?> removeSavedPost(@RequestBody SavedPostDto savedPostDto){
       return savedPostService.removeSavedPost(savedPostDto.getUserId(), savedPostDto.getPostId());
    }

    @PostMapping("/get")
     public ResponseEntity<List<SavedPostDto>> getSavedPost(@RequestBody SavedPostDto savedPostDto){
       return savedPostService.getSavedPost(savedPostDto.getUserId());
    }
}
