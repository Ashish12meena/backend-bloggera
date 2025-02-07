package com.example.Blogera_demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.Blogera_demo.dto.CountLikeComment;
import com.example.Blogera_demo.model.Like;
import com.example.Blogera_demo.repository.LikeRepository;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    @Lazy
    private CommentService commentService;
     public Like createLike(Like like) {
        // Create a new Like object
        like.setCreatedAt(LocalDateTime.now());
        // Save and return the like object
        return likeRepository.save(like);
    }

   public List<Like> getLikesForPost(String postId) {
        // Fetch likes based on postId
        return likeRepository.findByPostId(postId);
    }

    public List<Like> getLikesByUser(String userId) {
        // Fetch likes based on userId
        return likeRepository.findByUserId(userId);
    }

    public Long getLikesCountForPost(String postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean checkIfLiked(String postId, String userId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    public Like addLike(String postId, String userId) {
        Like like = new Like();
        like.setCreatedAt(LocalDateTime.now());
        like.setPostId(postId);
        like.setUserId(userId);

        return likeRepository.save(like);
    }

    public void removeLike(String postId, String userId) {
           likeRepository.deleteByUserIdAndPostId(userId,postId);
    }

    public CountLikeComment getCountLikeComment(String postId) {
        CountLikeComment countLikeComment = new CountLikeComment();
        long commentCount = commentService.getCommentCountForPost(postId);
        long likeCount = getLikesCountForPost(postId);
        countLikeComment.setLikeCount(likeCount);
        countLikeComment.setCommentCount(commentCount);
        return countLikeComment;
    }
}
