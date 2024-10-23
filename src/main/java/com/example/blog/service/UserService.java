package com.example.blog.service;

import com.example.blog.exception.NotAvailableException;
import com.example.blog.exception.NotPresentedException;
import com.example.blog.exception.WrongPasswordException;
import com.example.blog.model.User;
import com.example.blog.model.UserApi;
import com.example.blog.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepo userRepo;

    public User findUserByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(
                () -> new NotPresentedException("There is not user with such username: " + username)
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(
                () -> new NotPresentedException("There is not user with such username: " + username)
        );
    }

    @Override
    public UserApi findUserApiByUsername(String username) {
        return findUserByUsername(username).toApi();
    }

    @Override
    public UserApi login(String username, String password) {
        User user = findUserByUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (encoder.matches(password, user.getPassword())) {
            return user.toApi();
        } else {
            throw new WrongPasswordException("Wrong password");
        }
    }

    @Override
    public UserApi create(String username, String password) {
        try {
            findUserByUsername(username);
        } catch (NotPresentedException e) {
            User user = User.builder()
                    .username(username)
                    .password(new BCryptPasswordEncoder().encode(password))
                    .authorities("ROLE_USER")
                    .build();

            userRepo.save(user);

            return user.toApi();
        }

        throw new NotAvailableException("The user with such username already exists");
    }
}
