package com.example.Blogera_demo.authService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Blogera_demo.model.User;
import com.example.Blogera_demo.model.UserDetailImpl;
import com.example.Blogera_demo.repository.AuthRepository;


@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    AuthRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userRepository.findByEmail(username);
        if (user==null) {
            
            throw new UsernameNotFoundException("User Not Found");
        }

        return new UserDetailImpl(user);
    }
    
}