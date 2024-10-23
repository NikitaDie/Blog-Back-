package com.example.blog.service;

import com.example.blog.model.Post;
import com.example.blog.model.PostApi;
import com.example.blog.repository.InMemoryPostDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InMemoryPostService implements IPostService {
    @Autowired
    private InMemoryPostDAO repository;

    @Override
    public List<PostApi> getPosts(int limit) {
        return repository.getPosts(limit).stream()
                .map(Post::toApi)
                .collect(Collectors.toList());
    }

    @Override
    public PostApi getPost(long id) {
        return null; //TODO: implementieren
    }

    @Override
    public PostApi getPost(String name) {
        return null;
    }

    @Override
    public Post createPost(PostApi newPost) {
        repository.addPost(new Post(newPost));
        return new Post(newPost);    //TODO
    }

    @Override
    public void updatePost(PostApi updatedPost) {

    }

    @Override
    public void deletePost(long id) {

    }

}