package com.example.Blogera_demo.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.Blogera_demo.dto.FindUserByEmial;
import com.example.Blogera_demo.dto.GetBYEmailAndUserId;
import com.example.Blogera_demo.dto.GetUserCardDetails;
import com.example.Blogera_demo.dto.TokenFcmDto;
import com.example.Blogera_demo.dto.UserDetails;
import com.example.Blogera_demo.model.Post;

import com.example.Blogera_demo.model.User;

import com.example.Blogera_demo.repository.UserRepository;
import com.example.Blogera_demo.serviceInterface.UserServiceInterface;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;
    

    @Autowired
    private MongoTemplate mongoTemplate;

    // Create a new user
    @Override
    public User createUser(User user) {
        // Ensure user object is not null and has required fields
        if (user == null || user.getUsername() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Invalid user data");
        }
        return userRepository.save(user);
    }

    // Retrieve a user by ID
    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Retrieve all users
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<String> getAllUserId() {
        return userRepository.findAll()
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    // Update a user
    @Override
    public User updateUser(String id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with ID " + id + " not found");
        }
        User user = optionalUser.get();
        // Ensure user details are valid
        if (userDetails.getUsername() != null)
            user.setUsername(userDetails.getUsername());
        if (userDetails.getEmail() != null)
            user.setEmail(userDetails.getEmail());
        if (userDetails.getProfilePicture() != null)
            user.setProfilePicture(userDetails.getProfilePicture());
        return userRepository.save(user);
    }

    // Delete a user
    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public ResponseEntity<?> getUserCardData(GetBYEmailAndUserId request) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        User user = optionalUser.get();
        List<Post> posts = postService.getPostsByUserId(user.getId());
        boolean followStatus = followService.isFollowing(request.getUserId(), user.getId());

        // Always return user details, even if posts are empty
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("user", Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "userId", user.getId(),
                "followStatus", followStatus,
                "followerCount", user.getFollowerCount(),
                "followingCount", user.getFollowingCount(),
                "postCount",user.getPostCount(),
                "userBio", Optional.ofNullable(user.getBio()).orElse(""),
                "dateOfBirth", Optional.ofNullable(user.getDateOfBirth()),
                "profilePicture", Optional.ofNullable(user.getProfilePicture()).orElse("")));
        responseBody.put("posts", new ArrayList<>()); // Default to an empty array

        if (posts == null || posts.isEmpty()) {
            return ResponseEntity.ok(responseBody); // ✅ Return user with empty posts array
        }

        List<String> postIds = posts.stream().map(Post::getId).distinct().toList();

        // Fetch likes asynchronously
        CompletableFuture<Map<String, Boolean>> likeFuture = CompletableFuture.supplyAsync(
                () -> likeService.getLikeStatus(user.getId(), postIds), executor);

        // Fetch post details asynchronously
        List<CompletableFuture<GetUserCardDetails>> postFutures = posts.stream()
                .map(post -> CompletableFuture.supplyAsync(() -> {

                    boolean likeStatus = likeFuture.join().getOrDefault(post.getId(), false);

                    GetUserCardDetails getCardDetails = new GetUserCardDetails();
                    getCardDetails.setPostId(post.getId());
                    getCardDetails.setCommentCount(post.getCommentCount());
                    getCardDetails.setLikeCount(post.getLikeCount());
                    getCardDetails.setPostContent(post.getContent());
                    getCardDetails.setPostImage(null);
                    getCardDetails.setPostTitle(post.getTitle());
                    getCardDetails.setLikeStatus(likeStatus);

                    return getCardDetails;
                }, executor))
                .collect(Collectors.toList());

        // Wait for all futures to complete
        List<GetUserCardDetails> getCardDetailsList = postFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        responseBody.put("posts", getCardDetailsList); // ✅ Only replace empty posts if there are actual posts

        return ResponseEntity.ok().body(responseBody);
    }

    @Override
    public UserDetails validateToken(FindUserByEmial findUserByEmial) {
        User user = getUserByEmail(findUserByEmial.getEmail());
        UserDetails userDetails = new UserDetails();
        userDetails.setEmail(user.getEmail());
        userDetails.setUserId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setProfilePicture(user.getProfilePicture());
        return userDetails;
    }

    @Override
    public void saveFCMToken(TokenFcmDto fcmTokenDto) {
        Query query = new Query(Criteria.where("_id").is(fcmTokenDto.getUserId()));
        Update update = new Update().set("fcmWebToken", fcmTokenDto.getFcmToken());

        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public List<User> getUsersByIds(List<String> userIds) {
        return userRepository.findAllById(userIds);
    }

    @Override
    public void incrementFollowerCount(String userId) {

        Query query = new Query(Criteria.where("id").is(userId));
        Update update = new Update().inc("followerCount", 1); // Increment likeCount by 1
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public void decrementFollowerCount(String userId) {

        Query query = new Query(Criteria.where("id").is(userId));
        Update update = new Update().inc("followerCount", -1); // Increment likeCount by 1
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public void incrementFollowingCount(String userId) {

        Query query = new Query(Criteria.where("id").is(userId));
        Update update = new Update().inc("followingCount", 1); // Increment likeCount by 1
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public void decrementFollowingCount(String userId) {

        Query query = new Query(Criteria.where("id").is(userId));
        Update update = new Update().inc("followingCount", -1); // Increment likeCount by 1
        mongoTemplate.updateFirst(query, update, User.class);
    }
    
    public void incrementPostCount(String userId) {
        Query query = new Query(Criteria.where("id").is(userId));
        Update update = new Update().inc("postCount", 1); // Increment likeCount by 1
        mongoTemplate.updateFirst(query, update, User.class);
    }

    
    public void decrementPostCount(String userId) {

        Query query = new Query(Criteria.where("id").is(userId));
        Update update = new Update().inc("postCount", -1); // Increment likeCount by 1
        mongoTemplate.updateFirst(query, update, User.class);
    }
}
