package com.example.Blogera_demo.service;

import com.example.Blogera_demo.dto.CommentCardDetails;
import com.example.Blogera_demo.dto.CommentDto;
import com.example.Blogera_demo.exceptions.ResourceNotFoundException;
import com.example.Blogera_demo.model.Comment;
import com.example.Blogera_demo.model.User;
import com.example.Blogera_demo.repository.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private PostService postService;

    @Autowired
    MongoTemplate mongoTemplate;

    // Create a new comment
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // Retrieve a comment by ID
    public Comment getCommentById(String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.get();
    }

    // Retrieve all comments
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    // Retrieve comments by userId
    public List<Comment> getCommentsByUserId(String userId) {
        return commentRepository.findByUserId(userId);
    }

    // Retrieve comments by postId
    public List<Comment> getCommentsByPostId(String postId) {
        return commentRepository.findByPostId(postId);
    }

    // Update a comment
    public Comment updateComment(String id, Comment commentDetails) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found for this id :: " + id));
        comment.setPostId(commentDetails.getPostId());
        comment.setUserId(commentDetails.getUserId());
        comment.setMessage(commentDetails.getMessage());
        return commentRepository.save(comment);
    }

    // Delete a comment
    public void deleteComment(String id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found for this id :: " + id));
        commentRepository.delete(comment);
    }

    public long getCommentCountForPost(String postId) {
        return commentRepository.countByPostId(postId);
    }

    public CommentCardDetails getCommentCardDetails(String commentId) {
        CommentCardDetails commentCardDetails = new CommentCardDetails();
        Comment comment = getCommentById(commentId);
        Optional<User> optionaluser = userService.getUserById(comment.getUserId());
        if (!optionaluser.isPresent()) {
            return null;
        }
        User user = optionaluser.get();
        commentCardDetails.setUsername(user.getUsername());
        commentCardDetails.setProfilePicture(user.getProfilePicture());
        commentCardDetails.setMessage(comment.getMessage());
        commentCardDetails.setLocalDateTime(comment.getLocalDateTime());

        return commentCardDetails;
    }

    public Comment addComment(CommentDto commentDto) {
        System.out.println("in addComment" + commentDto.getUserId());
        Comment comment = new Comment();
        // postService.getPostsByPostId();
        comment.setLocalDateTime(LocalDateTime.now());
        comment.setMessage(commentDto.getMessage());
        comment.setPostId(commentDto.getPostId());
        comment.setUserId(commentDto.getUserId());
         postService.incrementCommentCount(commentDto.getPostId());
        return commentRepository.save(comment);

    }
}