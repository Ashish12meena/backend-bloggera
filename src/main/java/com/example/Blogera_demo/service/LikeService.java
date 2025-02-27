package com.example.Blogera_demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;




import org.springframework.stereotype.Service;

import com.example.Blogera_demo.dto.CountLikeComment;
import com.example.Blogera_demo.model.Like;

import com.example.Blogera_demo.repository.LikeRepository;
import com.example.Blogera_demo.serviceInterface.LikeServiceInterface;

@Service
public class LikeService implements LikeServiceInterface {

    @Autowired
    private LikeRepository likeRepository;

    

    @Autowired
    @Lazy
    PostService postService;

    @Autowired
    @Lazy
    private CommentService commentService;

    //Create Like Document
    @Override
    public Like createLike(Like like) {
        // Create a new Like object
        like.setCreatedAt(LocalDateTime.now());
        // Save and return the like object
        return likeRepository.save(like);
    }

    //get List of Like document for single post
    @Override
    public List<Like> getLikesForPost(String postId) {
        // Fetch likes based on postId
        return likeRepository.findByPostId(postId);
    }

    //get list of like by user 
    @Override
    public List<Like> getLikesByUser(String userId) {
        // Fetch likes based on userId
        return likeRepository.findByUserId(userId);
    }

    @Override
    public Long getLikesCountForPost(String postId) {
        return likeRepository.countByPostId(postId);
    }

    @Override
    public boolean checkIfLiked(String postId, String userId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    //get map of Like Status which have combination of postId and boolean value
    @Override
    public Map<String, Boolean> getLikeStatus(String userId, List<String> postIds) {
        List<Like> likedPosts = likeRepository.findByUserIdAndPostIdIn(userId, postIds);
        Map<String, Boolean> likeStatusMap = likedPosts.stream()
                .collect(Collectors.toMap(Like::getPostId, _ -> true));

        for (String postId : postIds) {
            likeStatusMap.putIfAbsent(postId, false);
        }

        return likeStatusMap;
    }

    //Add Like document
    @Override
    public Like addLike(String postId, String userId) {
        Like like = new Like();
        like.setCreatedAt(LocalDateTime.now());
        like.setPostId(postId);
        like.setUserId(userId);

        postService.incrementLikeCount(postId);

        return likeRepository.save(like);
    }

    @Override
    public void removeLike(String postId, String userId) {
        postService.decrementLikeCount(postId);
        System.out.println("Like Count decerem");
        likeRepository.deleteByUserIdAndPostId(userId, postId);

    }

    // get count of like and comment of post
    @Override
    public CountLikeComment getCountLikeComment(String postId) {
        CountLikeComment countLikeComment = new CountLikeComment();
        long commentCount = commentService.getCommentCountForPost(postId);
        long likeCount = getLikesCountForPost(postId);
        countLikeComment.setLikeCount(likeCount);
        countLikeComment.setCommentCount(commentCount);
        return countLikeComment;
    }
}
