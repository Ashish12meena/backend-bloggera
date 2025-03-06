package com.example.Blogera_demo.serviceInterface;

import java.util.List;

import java.util.Set;
import java.util.concurrent.ExecutionException;


import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.Blogera_demo.dto.ExcludedIds;
import com.example.Blogera_demo.dto.GetAllPostCardDetails;
import com.example.Blogera_demo.dto.GetFullPostDetail;
import com.example.Blogera_demo.model.Post;


public interface PostServiceInterface {
    Post createPost(String userId, Post post);
    List<Post> getPostsByUserId(String email);
    Post getPostsByPostId(String postId);
    GetFullPostDetail getFullPostDetails(String postId, String currentUserId);
    List<GetAllPostCardDetails> getCardDetails(String currentUserId,Set<String> excludedIds, List<String> categories);
    List<GetAllPostCardDetails> getData();
    void incrementLikeCount(String postId);
    void decrementLikeCount(String postId);
    void incrementCommentCount(String postId);
    void decrementCommentCount(String postId);
    ResponseEntity<String> addPost(String userId, Post post, MultipartFile postImage);
    List<Post> getAllPosts(Set<String> excludedIds);
    List<Post> getAllFilteredPosts(Set<String> excludedIds, List<String> categories) throws InterruptedException, ExecutionException;
    List<Post> getRandomPosts(Set<String> excludedIds);
    List<Post> getMostLikedPosts(Set<String> excludedIds);
    List<Post> getMostCommentedPosts(Set<String> excludedIds);
    List<Post> getMostSimilartoCategoryPosts(Set<String> excludedIds, List<String> categories);
    List<Post> searchByCategoriesThenText(Set<String> excludedIds, List<String> categories, String searchText, Integer sample);
    List<Post> searchByText(Set<String> excludedIds, String searchText, Integer sample);
    List<GetAllPostCardDetails> searchPostByText(ExcludedIds excludedIds);
    List<Post> getPostsByPostIds(List<String> postIds);

}
