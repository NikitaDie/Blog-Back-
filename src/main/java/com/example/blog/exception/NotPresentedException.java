package com.example.blog.exception;

import jakarta.persistence.EntityNotFoundException;

public class NotPresentedException extends EntityNotFoundException {
    public NotPresentedException(String message) {
        super(message);
    }
}
