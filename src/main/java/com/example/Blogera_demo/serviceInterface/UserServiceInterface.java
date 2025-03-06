package com.example.Blogera_demo.serviceInterface;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.example.Blogera_demo.dto.FindUserByEmial;
import com.example.Blogera_demo.dto.GetBYEmailAndUserId;
import com.example.Blogera_demo.dto.TokenFcmDto;
import com.example.Blogera_demo.dto.UserDetails;
import com.example.Blogera_demo.model.User;

public interface UserServiceInterface {
    User createUser(User user);

    Optional<User> getUserById(String id);

    List<User> getAllUsers();

    List<String> getAllUserId();

    List<User> getUsersByIds(List<String> userIds);
 
    User updateUser(String id, User userDetails);

    void deleteUser(String id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    ResponseEntity<?> getUserCardData(GetBYEmailAndUserId request);

    UserDetails validateToken(FindUserByEmial findUserByEmial);

    void saveFCMToken(TokenFcmDto fcmTokenDto);

    void incrementFollowerCount(String userId);

    void decrementFollowerCount(String userId);

    void incrementFollowingCount(String userId);

    void decrementFollowingCount(String userId);

}
