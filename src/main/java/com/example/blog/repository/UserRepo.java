package com.example.blog.repository;

import com.example.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
    public User findByVerificationCode(String code);
    Optional<User> findByLogin(String login);
    Optional<User> findByUsername(String username);
}