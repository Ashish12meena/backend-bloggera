package com.example.Blogera_demo.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Blogera_demo.dto.FindByUserId;
import com.example.Blogera_demo.dto.GetAllPostCardDetails;
import com.example.Blogera_demo.dto.GetFullPostDetail;
import com.example.Blogera_demo.dto.LikeDetails;
import com.example.Blogera_demo.model.Post;
import com.example.Blogera_demo.service.PostService;

@CrossOrigin(origins = "*", allowedHeaders = "Authorization")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/user/{userId}")
    public Post createPost(@PathVariable String userId, @RequestBody Post post) {
        
        return postService.createPost(userId, post);
    }

    @PostMapping("/cardDetails")
    public List<GetAllPostCardDetails> getCartDetails(@RequestBody FindByUserId findByUserId ){
        
        System.out.println("count time");
        long startTime = System.currentTimeMillis();
        List<GetAllPostCardDetails> getAllPostCardDetails = postService.getCardDetails(findByUserId.getUserId());
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
        return getAllPostCardDetails;
    }
    @PostMapping("/data")
    public List<GetAllPostCardDetails> getData(){
        System.out.println("count time");
        long startTime = System.currentTimeMillis();
        List<GetAllPostCardDetails> getAllPostCardDetails = postService.getData();
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
        return getAllPostCardDetails;
    }
    @PostMapping("/fullPostDetails")
    public GetFullPostDetail getFullPostDetails(@RequestBody LikeDetails likedetails){
        
        
        return postService.getFullPostDetails(likedetails.getPostId(),likedetails.getUserId());
    }
 
    
  
}
