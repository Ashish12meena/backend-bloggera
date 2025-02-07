package com.example.Blogera_demo.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.Blogera_demo.dto.FindUserByEmial;
import com.example.Blogera_demo.dto.GetUserCardDetails;
import com.example.Blogera_demo.dto.UserDetails;
import com.example.Blogera_demo.model.Post;
import com.example.Blogera_demo.model.PostImage;
import com.example.Blogera_demo.model.User;
import com.example.Blogera_demo.repository.PostImageReposiroty;
import com.example.Blogera_demo.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostImageReposiroty postImageReposiroty;

    // Create a new user
    public User createUser(User user) {
        // Ensure user object is not null and has required fields
        if (user == null || user.getUsername() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Invalid user data");
        }
        return userRepository.save(user);
    }

    // Retrieve a user by ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Retrieve all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update a user
    public User updateUser(String id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with ID " + id + " not found");
        }
        User user = optionalUser.get();
        // Ensure user details are valid
        if (userDetails.getUsername() != null) user.setUsername(userDetails.getUsername());
        if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
        if (userDetails.getProfilePicture() != null) user.setProfilePicture(userDetails.getProfilePicture());
        return userRepository.save(user);
    }

    // Delete a user
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        
        return userRepository.findByEmail(email).orElse(null);
    }

    public ResponseEntity<?> getUserCardData(String email) {
        

        // Safely retrieve user by email
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        User user = optionalUser.get();
        List<Post> posts = postService.getPostsByUserId(user.getId());
        

        // Handle empty or null posts
        if (posts == null || posts.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // Return empty list instead of casting
        }

        // Safely process each post
        List<GetUserCardDetails> getCardDetailsList = posts.stream().map(post -> {
            
            // Ensure the repository call doesn't return null
            List<String> images = Optional.ofNullable(postImageReposiroty.findByPostId(post.getId()))
                                          .orElse(Collections.emptyList())
                                          .stream()
                                          .map(PostImage::getImageUrl)
                                          .collect(Collectors.toList());

            
            String firstImage = images.isEmpty() ? null : images.get(0);
            
            long countLike = likeService.getLikesCountForPost(post.getId());
            
            long countComment = commentService.getCommentCountForPost(post.getId());

            GetUserCardDetails getCardDetails = new GetUserCardDetails();
            getCardDetails.setPostId(post.getId());
            getCardDetails.setCommentCount(countComment);
            getCardDetails.setLikeCount(countLike);
            getCardDetails.setPostContent(post.getContent());
            getCardDetails.setPostImage(firstImage);
            getCardDetails.setPostTitle(post.getTitle());

            
            return getCardDetails;
        }).collect(Collectors.toList());

        // Safely prepare response
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("user", Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "profilePicture", Optional.ofNullable(user.getProfilePicture()).orElse("")
        ));
        responseBody.put("posts", getCardDetailsList);

        return ResponseEntity.ok().body(responseBody);
    }

    public UserDetails validateToken(FindUserByEmial findUserByEmial) {
        User user = getUserByEmail(findUserByEmial.getEmail());
        UserDetails userDetails = new UserDetails();
        userDetails.setEmail(user.getEmail());
        userDetails.setUserId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setProfilePicture(user.getProfilePicture());
        return userDetails;
    }
}
