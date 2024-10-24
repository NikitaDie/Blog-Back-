package com.example.blog.service;

import com.example.blog.model.User;
import com.example.blog.model.UserApi;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    UserApi login(String login, String password);
    UserApi create(UserApi newUser);
    UserApi findUserById(long id);
    User findUserByLogin(String username);
}
