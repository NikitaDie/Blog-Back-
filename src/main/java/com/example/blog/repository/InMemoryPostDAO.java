package com.example.blog.repository;

import com.example.blog.model.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryPostDAO {
    private final List<Post> posts = new ArrayList<>();

    public List<Post> getPosts(int limit) {
        limit = Math.min(limit, posts.size());
        return limit == 0 ? posts : new ArrayList<>(posts.subList(0, limit));
    }

    public void addPost(Post post) {
        posts.add(post);
    }
}
