package com.example.blog.service;

import com.example.blog.model.User;
import com.example.blog.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found or not enabled"));

        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User not found or not enabled");
        }

        return user;
    }
}
