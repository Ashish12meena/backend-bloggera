package com.example.Blogera_demo.serviceInterface;

import java.util.List;

import com.example.Blogera_demo.dto.CommentCardDetails;
import com.example.Blogera_demo.dto.CommentDto;
import com.example.Blogera_demo.model.Comment;

public interface CommentServiceInterface {
    Comment createComment(Comment comment);
    Comment getCommentById(String id);
    List<Comment> getAllComments();
    List<Comment> getCommentsByUserId(String userId);
    List<Comment> getCommentsByPostId(String postId);
    Comment updateComment(String id, Comment commentDetails);
    void deleteComment(String id);
    long getCommentCountForPost(String postId);
    CommentCardDetails getCommentCardDetails(String commentId);
    Comment addComment(CommentDto commentDto);
}
