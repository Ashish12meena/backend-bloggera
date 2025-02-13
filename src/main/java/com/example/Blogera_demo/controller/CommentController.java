package com.example.Blogera_demo.controller;

import com.example.Blogera_demo.dto.CommentCardDetails;
import com.example.Blogera_demo.dto.CommentDto;
import com.example.Blogera_demo.model.Comment;
import com.example.Blogera_demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public Comment createComment(@RequestBody Comment comment) {
        return commentService.createComment(comment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String id) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok().body(comment);
    }

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/user/{userId}")
    public List<Comment> getCommentsByUserId(@PathVariable String userId) {
        return commentService.getCommentsByUserId(userId);
    }

    @GetMapping("/post/{postId}")
    public List<Comment> getCommentsByPostId(@PathVariable String postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @RequestBody Comment commentDetails) {
        Comment updatedComment = commentService.updateComment(id, commentDetails);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/commentCard")
    public CommentCardDetails getCommentCardDetails(@RequestBody Map<String, String> request){
        String commentId  = request.get("commentId");
        return commentService.getCommentCardDetails(commentId);
    }


    @PostMapping("/addComment")
    public Comment addComment(@RequestBody CommentDto commentDto){
        System.out.println(commentDto.getMessage()+"  "+commentDto.getPostId()+" before"+commentDto.getUserId()+" after");
        return commentService.addComment(commentDto);
        
    }

}