package com.example.blog.service;
import com.example.blog.model.Post;
import com.example.blog.model.PostApi;
import java.util.List;

public interface IPostService {
    public List<PostApi> getPosts(int limit);
    public PostApi getPost(long id);
    public PostApi getPost(String name);
    public Post createPost(PostApi newPost);
    public void updatePost(PostApi updatedPost);
    public void deletePost(long id);
}
