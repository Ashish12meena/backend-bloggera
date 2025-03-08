package com.example.Blogera_demo.service;

import com.example.Blogera_demo.dto.CommentCardDetails;
import com.example.Blogera_demo.dto.CommentDto;
import com.example.Blogera_demo.dto.NotificationDto;
import com.example.Blogera_demo.exceptions.ResourceNotFoundException;
import com.example.Blogera_demo.model.Comment;
import com.example.Blogera_demo.model.User;
import com.example.Blogera_demo.repository.CommentRepository;
import com.example.Blogera_demo.serviceInterface.CommentServiceInterface;
import com.example.Blogera_demo.utility.ActionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService implements CommentServiceInterface {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private PostService postService;

    @Autowired
    @Lazy
    private NotificationService notificationService;

    @Autowired
    MongoTemplate mongoTemplate;

    // Create a new comment
    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // Retrieve a comment by ID
    @Override
    public Comment getCommentById(String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.get();
    }

    // Retrieve all comments
    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    // Retrieve comments by userId
    @Override
    public List<Comment> getCommentsByUserId(String userId) {
        return commentRepository.findByUserId(userId);
    }

    // Retrieve comments by postId
    @Override
    public List<Comment> getCommentsByPostId(String postId) {
        return commentRepository.findByPostId(postId);
    }

    // Update a comment
    @Override
    public Comment updateComment(String id, Comment commentDetails) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found for this id :: " + id));
        comment.setPostId(commentDetails.getPostId());
        comment.setUserId(commentDetails.getUserId());
        comment.setMessage(commentDetails.getMessage());
        return commentRepository.save(comment);
    }

    // Delete a comment
    @Override
    public void deleteComment(String id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found for this id :: " + id));
        commentRepository.delete(comment);
    }

    // get Comment Count By post
    @Override
    public long getCommentCountForPost(String postId) {
        return commentRepository.countByPostId(postId);
    }

    // get Comment Card Details
    @Override
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

    // Add Comment
    @Override
    @Transactional // Ensures atomicity
    public Comment addComment(CommentDto commentDto) {

        // Create and save the comment
        Comment comment = new Comment();
        comment.setLocalDateTime(LocalDateTime.now()); // Ensure correct setter
        comment.setMessage(commentDto.getCommentText());
        comment.setPostId(commentDto.getPostId());
        comment.setUserId(commentDto.getUserId());

        Comment savedComment = commentRepository.save(comment);

        // Increment comment count
        postService.incrementCommentCount(commentDto.getPostId());

        // Prepare notification
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setActionType(ActionType.COMMENT);
        notificationDto.setCommentText(commentDto.getCommentText());
        notificationDto.setPostId(commentDto.getPostId());
        notificationDto.setUserId(commentDto.getUserId());

        // Send notification with error handling
        try {
            notificationService.addNotification(notificationDto);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return savedComment;
    }

}