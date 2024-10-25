package com.example.blog.controller;

import com.example.blog.model.UserApi;
import com.example.blog.service.IUserService;
import com.example.blog.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
        UserApi user = service.login(request.getLogin(), request.getPassword());
        if (user != null) {
            String token = jwtUtil.generateToken(request.getLogin());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody UserApi user, HttpServletRequest request) {
        UserApi newUser = service.create(user, getSiteURL(request));
        if (newUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (service.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }

}
