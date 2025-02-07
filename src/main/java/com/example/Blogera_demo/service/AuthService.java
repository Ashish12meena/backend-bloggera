    package com.example.Blogera_demo.service;

    import java.time.LocalDateTime;
    import java.util.HashMap;
    import java.util.Map;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.stereotype.Service;

    import com.example.Blogera_demo.authService.TokenService;
    import com.example.Blogera_demo.dto.Login;
    import com.example.Blogera_demo.dto.Response;
import com.example.Blogera_demo.dto.UserDetails;
import com.example.Blogera_demo.model.User;
    import com.example.Blogera_demo.repository.AuthRepository;

    @Service
    public class AuthService {

        @Autowired
        private AuthRepository userRepository;

        @Autowired
        private UserService userService;

        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        private TokenService tokenService;

        private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(5);

        public ResponseEntity<?> createUser(User user) {
            Map<String, Object> responseBody = new HashMap<>();
            if (userRepository.existsByEmail(user.getEmail())) {
                // Email already exists, return custom error response
                responseBody.put("message", new Response("error", "User already exists"));
                return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
            }
            user.setPassword(encoder.encode(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            String jwt = tokenService.generateToken(user.getEmail());
            // Return success response
            responseBody.put("message", new Response("ok", "User Register Successfully"));
            responseBody.put("authToken", jwt);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        }

        public ResponseEntity<?> loginUser(Login user) {
            UserDetails userDetails = new UserDetails();
            
            Map<String, Object> responseBody = new HashMap<>();
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            if (authentication.isAuthenticated()) {
                String authToken = tokenService.generateToken(user.getEmail());
            User sendUser =  userService.getUserByEmail(user.getEmail());
            userDetails.setEmail(sendUser.getEmail());
            userDetails.setUserId(sendUser.getId());
            userDetails.setUsername(sendUser.getUsername());
            userDetails.setProfilePicture(sendUser.getProfilePicture());

                responseBody.put("authToken",authToken);
                responseBody.put("user", userDetails);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
                // return null;
            }else{

                responseBody.put("message", "User is not authenticated");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
            }   
        }
    }
