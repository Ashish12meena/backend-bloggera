package com.example.Blogera_demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Blogera_demo.dto.CountLikeComment;
import com.example.Blogera_demo.dto.FindByPostId;
import com.example.Blogera_demo.dto.LikeDetails;
import com.example.Blogera_demo.model.Like;
import com.example.Blogera_demo.service.LikeService;

@RestController
@RequestMapping("api/like")
public class LikeController {
    @Autowired
    LikeService likeService;

    @PostMapping
    public ResponseEntity<Like> createLike(@RequestBody Like like){
        Like liked = likeService.createLike(like);
        return ResponseEntity.status(HttpStatus.CREATED).body(liked);
    }

    @PostMapping("/likeStatus")
    public boolean checkIfLiked(@RequestBody LikeDetails likeDetails){
        
        return likeService.checkIfLiked(likeDetails.getPostId(),likeDetails.getUserId());
    }

    @PostMapping("/addlike")
    public boolean addLike(@RequestBody LikeDetails likeDetails){
        
        Like like = likeService.addLike(likeDetails.getPostId(),likeDetails.getUserId());
        if (like!=null) {
            return true;
        }
        return false;
    }

    @PostMapping("/removelike")
    public boolean removeLike(@RequestBody LikeDetails likeDetails){
        
        likeService.removeLike(likeDetails.getPostId(),likeDetails.getUserId());
        return true;
    }

    @PostMapping("/getCount")
    public CountLikeComment countLikeComment(@RequestBody FindByPostId findByPostId){
        return likeService.getCountLikeComment(findByPostId.getPostId());
    }
    
}
