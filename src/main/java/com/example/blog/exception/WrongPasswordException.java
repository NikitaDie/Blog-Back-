package com.example.blog.exception;

public class WrongPasswordException extends UnauthorizedException {
    public WrongPasswordException(String message) {
        super(message);
    }
}
