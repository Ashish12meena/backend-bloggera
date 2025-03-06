package com.example.Blogera_demo.serviceInterface;

import java.util.List;
import java.util.Map;

import com.example.Blogera_demo.dto.CountLikeComment;
import com.example.Blogera_demo.model.Like;

public interface LikeServiceInterface {
    Like createLike(Like like);
    List<Like> getLikesForPost(String postId);
    List<Like> getLikesByUser(String userId);
    Like getLikeByUserIdAndPostId(String userId, String postId);
    Long getLikesCountForPost(String postId);
    boolean checkIfLiked(String postId, String userId);
    Map<String, Boolean> getLikeStatus(String userId, List<String> postIds);
    Like addLike(String postId, String userId);
    void removeLike(String postId, String userId);
    CountLikeComment getCountLikeComment(String postId);
}
