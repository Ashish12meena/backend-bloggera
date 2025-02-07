package com.example.Blogera_demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Blogera_demo.dto.GetAllPostCardDetails;
import com.example.Blogera_demo.dto.GetFullPostDetail;
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
    public List<GetAllPostCardDetails> getCartDetails(){
        System.out.println("count time");
        long startTime = System.currentTimeMillis();
        List<GetAllPostCardDetails> getAllPostCardDetails = postService.getCardDetails();
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
        return getAllPostCardDetails;
    }
    @PostMapping("/fullPostDetails")
    public GetFullPostDetail getFullPostDetails(@RequestBody Map<String, String> request){
        String postId  = request.get("postId");
        return postService.getFullPostDetails(postId);
    }
 
    
  
}
