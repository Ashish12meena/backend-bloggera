package com.example.Blogera_demo.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Blogera_demo.dto.ExcludedIds;
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
    public List<GetAllPostCardDetails> getCartDetails(@RequestBody ExcludedIds excludedIds) {
        List<GetAllPostCardDetails> getAllPostCardDetails = postService.getCardDetails(excludedIds.getUserId(),excludedIds.getExcludedIds(),excludedIds.getListOfCategories());
        return getAllPostCardDetails;
    }

    @PostMapping("/data")
    public List<GetAllPostCardDetails> getData() {
        List<GetAllPostCardDetails> getAllPostCardDetails = postService.getData();
        return getAllPostCardDetails;
    }

    @PostMapping("/fullPostDetails")
    public GetFullPostDetail getFullPostDetails(@RequestBody LikeDetails likedetails) {

        return postService.getFullPostDetails(likedetails.getPostId(), likedetails.getUserId());
    }

    @PostMapping("/addPost")
    public ResponseEntity<?> addPost(
            @RequestParam("userId") String userId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") List<String> category,
            @RequestPart(value = "postImage", required = false) MultipartFile postImage) {

        Post post = new Post();
        post.setContent(content);
        post.setTitle(title);
        post.setCategories(category);
        return postService.addPost(userId, post, postImage);

    }

    @PostMapping("/getAllPost")
    public List<Post> getAllPosts(@RequestBody ExcludedIds excludedIds) {
        return postService.getAllPosts(excludedIds.getExcludedIds());
    }

    @PostMapping("/getMostLiked")
    public List<Post> getMostLikedPosts(@RequestBody ExcludedIds excludedIds) {
        return postService.getMostLikedPosts(excludedIds.getExcludedIds());
    }

    @PostMapping("/getMostComment")
    public List<Post> getMostCommentPosts(@RequestBody ExcludedIds excludedIds) {
        return postService.getMostCommentedPosts(excludedIds.getExcludedIds());
    }


    @PostMapping("/getAllPosts")
    public List<Post> getAllFilteredPostss(@RequestBody ExcludedIds excludedIds) {
        List<Post> filteredPost = new ArrayList<>();
        
            try {
                filteredPost = postService.getAllFilteredPosts(excludedIds.getExcludedIds(),
                        excludedIds.getListOfCategories());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return filteredPost;
        
    }

    @PostMapping("/getRandom")
    public List<Post> getRandomPost(@RequestBody ExcludedIds excludedIds) {
        return postService.getRandomPosts(excludedIds.getExcludedIds());

    }
    @PostMapping("/search")
    public List<GetAllPostCardDetails> searchPosts(@RequestBody ExcludedIds excludedIds) {
        return postService.searchPostByText(excludedIds);
        // return null;
    }

}   
