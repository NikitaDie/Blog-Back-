package com.example.blog.exception;

public class PostException extends RuntimeException
{
    PostException(String errorMessage)
    {
        super(errorMessage);
    }

    PostException(String errorMessage, Throwable err)
    {
        super(errorMessage, err);
    }
}