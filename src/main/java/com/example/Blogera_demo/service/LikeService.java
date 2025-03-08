package com.example.Blogera_demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Blogera_demo.dto.CountLikeComment;
import com.example.Blogera_demo.dto.NotificationDto;
import com.example.Blogera_demo.model.Like;
import com.example.Blogera_demo.model.Post;
import com.example.Blogera_demo.repository.LikeRepository;
import com.example.Blogera_demo.serviceInterface.LikeServiceInterface;
import com.example.Blogera_demo.utility.ActionType;

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

    @Autowired
    @Lazy
    private NotificationService notificationService;

    // Create Like Document
    @Override
    public Like createLike(Like like) {
        // Create a new Like object
        like.setCreatedAt(LocalDateTime.now());
        // Save and return the like object
        return likeRepository.save(like);
    }

    // get List of Like document for single post
    @Override
    public List<Like> getLikesForPost(String postId) {
        // Fetch likes based on postId
        return likeRepository.findByPostId(postId);
    }

    // get list of like by user
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
    public Like getLikeByUserIdAndPostId(String userId, String postId) {
        return likeRepository.findByUserIdAndPostId(userId, postId).orElse(null);
    }

    @Override
    public boolean checkIfLiked(String postId, String userId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    // get map of Like Status which have combination of postId and boolean value
    @Override
    public Map<String, Boolean> getLikeStatus(String userId, List<String> postIds) {
        List<Like> likedPosts = likeRepository.findByUserIdAndPostIdIn(userId, postIds);
    
        @SuppressWarnings("unused")
        Map<String, Boolean> likeStatusMap = likedPosts.stream()
                .collect(Collectors.toMap(
                        Like::getPostId,
                        _ -> true,
                        (existing, replacement) -> existing // Handle duplicate keys by keeping one value
                ));
    
        for (String postId : postIds) {
            likeStatusMap.putIfAbsent(postId, false);
        }
    
        return likeStatusMap;
    }

    // Add Like document
    @Override
    @Transactional // Ensures all operations succeed together
    public Like addLike(String postId, String userId) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setActionType(ActionType.LIKE);
        notificationDto.setPostId(postId);
        notificationDto.setUserId(userId);
        // Check if the post exists before proceeding
        Post post = postService.getPostsByPostId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found with ID: " + postId);
        }

        // Create and save the like
        Like like = new Like();
        like.setCreatedAt(LocalDateTime.now());
        like.setPostId(postId);
        like.setUserId(userId);
        Like savedLike = likeRepository.save(like); // Save first

        // Increment like count in the post
        postService.incrementLikeCount(postId);

        // Now send the notification (after like is successfully saved)
        notificationService.addNotification(notificationDto);

        return savedLike;
    }

    @Override
    public void removeLike(String postId, String userId) {
        // Check if the like exists before attempting to remove it
        Like like = getLikeByUserIdAndPostId(userId, postId);

        if (like != null) {
            // Remove like and decrement count
            likeRepository.deleteByUserIdAndPostId(userId, postId);
            postService.decrementLikeCount(postId);
            // Delete associated notification
            notificationService.deleteNotification(userId, postId);
        } else {
        }
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
