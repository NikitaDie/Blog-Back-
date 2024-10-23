package com.example.blog.controller;

import com.example.blog.model.UserApi;
import com.example.blog.service.IUserService;
import com.example.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private IUserService service;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserApi request) {
        UserApi user = service.login(request.getUsername(), request.getPassword());
        if (user != null) {
            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody UserApi request) {
        UserApi newUser = service.create(request.getUsername(), request.getPassword());
        if (newUser != null) {
            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(token);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
