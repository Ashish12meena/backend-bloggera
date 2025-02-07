package com.example.Blogera_demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.Blogera_demo.repository.LikeRepository;
import com.example.Blogera_demo.repository.PostImageReposiroty;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostImageReposiroty imageReposiroty;

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

    public GetFullPostDetail getFullPostDetails(String postId) {

        GetFullPostDetail getFullPostDetail = new GetFullPostDetail();
        Post post = getPostsByPostId(postId);
        User user = userRepository.findById(post.getUserId()).orElse(null);

        List<String> images = imageReposiroty.findByPostId(post.getId()).stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());

        List<String> comments = commentRepository.findByPostId(post.getId()).stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        long countLike = likeRepository.countByPostId(post.getId());
        long countComment = commentRepository.countByPostId(post.getId());

        String username = Optional.ofNullable(user).map(User::getUsername).orElse("Unknown");
        String profilePicture = Optional.ofNullable(user).map(User::getProfilePicture).orElse("");

        getFullPostDetail.setComments(comments);
        getFullPostDetail.setCommentCount(countComment);
        getFullPostDetail.setLikeCount(countLike);
        getFullPostDetail.setPostContent(post.getContent());
        getFullPostDetail.setPostImage(images);
        getFullPostDetail.setPostTitle(post.getTitle());
        getFullPostDetail.setProfilePicture(profilePicture);
        getFullPostDetail.setUsername(username);

        return getFullPostDetail;

    }

    public List<GetAllPostCardDetails> getCardDetails() {
        List<GetAllPostCardDetails> getAllPostCardDetails = new ArrayList<>();
        List<Post> posts = postRepository.findAll();

        // Create a thread pool with a fixed number of threads
        ExecutorService executorService = Executors.newFixedThreadPool(4); // 4 threads for I/O tasks (user, images,
                                                                           // like, comment)

        List<Future<GetAllPostCardDetails>> futures = new ArrayList<>();

        for (Post post : posts) {
            futures.add(executorService.submit(() -> fetchUserForPost(post)));
        }

        // Collect results from futures
        for (Future<GetAllPostCardDetails> future : futures) {
            try {
                getAllPostCardDetails.add(future.get()); // Blocking call to get result
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown(); // Shutdown executor service
        return getAllPostCardDetails;
    }

    public GetAllPostCardDetails fetchUserForPost(Post post) {
        

        final User[] user = new User[1];
        final List<String>[] images = new List[1];
        final long[] countLike = new long[1];
        final long[] countComment = new long[1];

        // Use a fixed thread pool to execute tasks in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(4); // 4 tasks for user, images, like, comment

        // Use parallel tasks for each part
        Future<?> userFuture = executorService.submit(() -> {
            user[0] = userRepository.findById(post.getUserId()).orElse(null);
        });

        Future<?> imagesFuture = executorService.submit(() -> {
            images[0] = imageReposiroty.findByPostId(post.getId()).stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList());
        });

        Future<?> likeFuture = executorService.submit(() -> {
            countLike[0] = likeRepository.countByPostId(post.getId());
        });

        Future<?> commentFuture = executorService.submit(() -> {
            countComment[0] = commentRepository.countByPostId(post.getId());
        });

        try {
            // Wait for all tasks to complete
            userFuture.get();
            imagesFuture.get();
            likeFuture.get();
            commentFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Construct result after all tasks are completed
        String username = Optional.ofNullable(user[0]).map(User::getUsername).orElse("Unknown");
        String profilePicture = Optional.ofNullable(user[0]).map(User::getProfilePicture).orElse("");
        String firstImage = (images[0] == null || images[0].isEmpty()) ? null : images[0].get(0);

        GetAllPostCardDetails postDetails = new GetAllPostCardDetails();
        postDetails.setCommentCount(countComment[0]);
        postDetails.setLikeCount(countLike[0]);
        postDetails.setPostContent(post.getContent());
        postDetails.setPostImage(firstImage);
        postDetails.setPostTitle(post.getTitle());
        postDetails.setProfilePicture(profilePicture);
        postDetails.setUsername(username);
        postDetails.setPostId(post.getId());

        executorService.shutdown(); // Shutdown executor service
        return postDetails;
    }

}