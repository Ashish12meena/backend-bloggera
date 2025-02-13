package com.example.Blogera_demo.service;

import java.time.LocalDateTime;

import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.stream.Collectors;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.Blogera_demo.dto.GetAllPostCardDetails;
import com.example.Blogera_demo.dto.GetFullPostDetail;
import com.example.Blogera_demo.model.Comment;
import com.example.Blogera_demo.model.Post;
import com.example.Blogera_demo.model.PostImage;
import com.example.Blogera_demo.model.User;
import com.example.Blogera_demo.repository.PostRepository;
import com.example.Blogera_demo.repository.AuthRepository;
import com.example.Blogera_demo.repository.CommentRepository;
// import com.example.Blogera_demo.repository.LikeRepository;
import com.example.Blogera_demo.repository.PostImageReposiroty;

@Service
public class PostService {

    // private static final Logger logger =
    // LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostImageReposiroty imageReposiroty;

    @Autowired
    private LikeService likeService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    Executor taskExecutor;

    // private final ExecutorService executorService =
    // Executors.newFixedThreadPool(10);

    public Post createPost(String userId, Post post) {

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            post.setUserId(userOptional.get().getId()); // Set the user as the author of the post
            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());
            return postRepository.save(post); // Save post to MongoDB
        } else {
            // Handle user not found scenario
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }

    public List<Post> getPostsByUserId(String email) {
        return postRepository.findByUserId(email);
    }

    public Post getPostsByPostId(String postId) {
        Optional<Post> post = postRepository.findById(postId);
        return post.get();
    }

    public GetFullPostDetail getFullPostDetails(String postId, String currentUserId) {
        System.out.println("postId " + postId + " userId " + currentUserId);
        ExecutorService executor = Executors.newFixedThreadPool(4);

        CompletableFuture<Post> postFuture = CompletableFuture.supplyAsync(() -> getPostsByPostId(postId), executor);
        CompletableFuture<List<String>> imagesFuture = CompletableFuture.supplyAsync(() -> imageReposiroty
                .findByPostId(postId).stream().map(PostImage::getImageUrl).collect(Collectors.toList()), executor);

        CompletableFuture<List<String>> commentsFuture = CompletableFuture.supplyAsync(
                () -> commentRepository.findByPostId(postId).stream().map(Comment::getId).collect(Collectors.toList()),
                executor);

        CompletableFuture<Boolean> likeStatusFuture = CompletableFuture
                .supplyAsync(() -> likeService.checkIfLiked(postId, currentUserId), executor);

        Post post = postFuture.join();
        CompletableFuture<User> userFuture = CompletableFuture
                .supplyAsync(() -> userRepository.findById(post.getUserId()).orElse(null), executor);

        User user = userFuture.join();
        List<String> images = imagesFuture.join();
        List<String> comments = commentsFuture.join();
        boolean status = likeStatusFuture.join();
        System.out.println(status + " Status of like");

        executor.shutdown();

        String username = Optional.ofNullable(user).map(User::getUsername).orElse("Unknown");
        String profilePicture = Optional.ofNullable(user).map(User::getProfilePicture).orElse("");

        GetFullPostDetail getFullPostDetail = new GetFullPostDetail();
        getFullPostDetail.setComments(comments);
        getFullPostDetail.setCommentCount(post.getCommentCount());
        getFullPostDetail.setLikeCount(post.getLikeCount());
        getFullPostDetail.setPostContent(post.getContent());
        getFullPostDetail.setPostImage(images);
        getFullPostDetail.setPostTitle(post.getTitle());
        getFullPostDetail.setProfilePicture(profilePicture);
        getFullPostDetail.setUsername(username);
        getFullPostDetail.setLikeStatus(status);

        return getFullPostDetail;
    }

    public List<GetAllPostCardDetails> getCardDetails(String currentUserId) {

        // Fetch posts in a single query
        List<Post> posts = postRepository.findAll();
        if (posts.isEmpty())
            return Collections.emptyList(); // Return early if no posts

        // Extract user IDs & post IDs
        List<String> userIds = posts.stream().map(Post::getUserId).distinct().toList();
        List<String> postIds = posts.stream().map(Post::getId).distinct().toList();

        // Fetch users and like status concurrently
        ExecutorService executor = Executors.newFixedThreadPool(3); // Use limited threads
        CompletableFuture<Map<String, User>> userFuture = CompletableFuture.supplyAsync(
                () -> userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getId, user -> user)),
                executor);
        CompletableFuture<Map<String, Boolean>> likeFuture = CompletableFuture.supplyAsync(
                () -> likeService.getLikeStatus(currentUserId, postIds),
                executor);

        // Wait for both tasks to complete
        Map<String, User> userMap = userFuture.join();
        Map<String, Boolean> likeStatusMap = likeFuture.join();

        // Process posts in parallel
        List<GetAllPostCardDetails> postDetailsList = posts.parallelStream()
                .map(post -> {
                    User user = userMap.get(post.getUserId());
                    GetAllPostCardDetails details = new GetAllPostCardDetails();
                    details.setPostId(post.getId());
                    details.setPostTitle(post.getTitle());
                    details.setPostContent(post.getContent());
                    details.setPostImage(post.getPostImage());
                    details.setLikeCount(post.getLikeCount());
                    details.setCommentCount(post.getCommentCount());
                    details.setLikeStatus(likeStatusMap.getOrDefault(post.getId(), false));

                    if (user != null) {
                        details.setUsername(user.getUsername());
                        details.setProfilePicture(user.getProfilePicture());
                    }
                    return details;
                }).toList(); // Parallel processing for better performance

        executor.shutdown(); // Shutdown executor

        return postDetailsList;
    }

    public List<GetAllPostCardDetails> getData() {
        List<Post> posts = postRepository.findAll();
        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch all users in a single query to avoid N+1 problem
        List<String> userIds = posts.stream().map(Post::getUserId).toList();
        Map<String, User> userMap = userRepository.findAllById(userIds)
                .stream().collect(Collectors.toMap(User::getId, user -> user));

        // Process each post asynchronously using Spring's thread pool
        List<CompletableFuture<GetAllPostCardDetails>> futures = posts.stream()
                .map(post -> CompletableFuture.supplyAsync(() -> mapPostToDetails(post, userMap), taskExecutor))
                .toList();

        // Wait for all threads to complete and collect results
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private GetAllPostCardDetails mapPostToDetails(Post post, Map<String, User> userMap) {
        User user = userMap.get(post.getUserId());
        GetAllPostCardDetails details = new GetAllPostCardDetails();
        details.setCommentCount(post.getCommentCount());
        details.setLikeCount(post.getLikeCount());
        details.setPostContent(post.getContent());
        details.setPostId(post.getId());
        details.setPostImage(post.getPostImage());
        details.setPostTitle(post.getTitle());

        if (user != null) {
            details.setUsername(user.getUsername());
            details.setProfilePicture(user.getProfilePicture());
        }

        return details;
    }

    public void incrementLikeCount(String postId) {

        Query query = new Query(Criteria.where("id").is(postId));
        Update update = new Update().inc("likeCount", 1); // Increment likeCount by 1
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    public void decrementLikeCount(String postId) {
        Query query = new Query(Criteria.where("id").is(postId));
        Update update = new Update().inc("likeCount", -1); // Decrease likeCount by 1
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    public void incrementCommentCount(String postId) {
        Query query = new Query(Criteria.where("id").is(postId));
        Update update = new Update().inc("commentCount", 1); // Increment likeCount by 1
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    public void decrementCommentCount(String postId) {
        Query query = new Query(Criteria.where("id").is(postId));
        Update update = new Update().inc("commentCount", -1); // Increment likeCount by 1
        mongoTemplate.updateFirst(query, update, Post.class);
    }

}