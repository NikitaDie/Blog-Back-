package com.example.blog.service;

import com.example.blog.model.User;
import com.example.blog.model.UserApi;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    public UserApi findUserApiByUsername(String login);
    public UserApi login(String login, String password);
    public UserApi create(String login, String password);
    public User findUserByUsername(String username);
}
