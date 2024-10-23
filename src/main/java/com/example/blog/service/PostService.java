package com.example.blog.service;

import com.example.blog.exception.NotAvailableException;
import com.example.blog.exception.NotPresentedException;
import com.example.blog.model.Post;
import com.example.blog.model.PostApi;
import com.example.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Primary
public class PostService implements IPostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public List<PostApi> getPosts(int limit) {
        Pageable pageable = (limit > 0) ? PageRequest.of(0, limit) : Pageable.unpaged();
        return postRepository.findPosts(pageable)
                .stream()
                .map(Post::toApi)
                .collect(Collectors.toList());
    }

    @Override
    public PostApi getPost(long id) {
        Post requestedPost = postRepository.findById(id).
                orElseThrow(() -> new NotPresentedException("Post with the id: " + id + " wasn't found"));

        return requestedPost.toApi();
    }

    @Override
    public PostApi getPost(String title) {
        return Optional.ofNullable(postRepository.findPostByTitle(title))
                .map(Post::toApi)
                .orElseThrow(() -> new NotPresentedException("Post with the title: " + title + " wasn't found"));
    }

    @Override
    public Post createPost(PostApi newPost) {
        if (postRepository.findPostByTitle(newPost.getTitle()) != null) {
            throw new NotAvailableException("Post with Title: " + newPost.getTitle() + ", already exists");
        }
        return postRepository.save(new Post(newPost));
    }

    @Override
    public void updatePost(PostApi updatedPost) {
        long id = updatedPost.getId();
        if (!postRepository.existsById(id)) {
            throw new NotPresentedException("Post with the id: " + id + " doesn't exist");
        }
        postRepository.save(new Post(updatedPost));
    }

    @Override
    public void deletePost(long id) {
        if (!postRepository.existsById(id)) {
            throw new NotPresentedException("Post with the id: " + id + " doesn't exist");
        }
        postRepository.deleteById(id);
    }
}
