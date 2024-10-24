package com.example.blog.controller;

import com.example.blog.exception.UnauthorizedException;
import com.example.blog.model.PostApi;
import com.example.blog.model.User;
import com.example.blog.model.UserApi;
import com.example.blog.service.IPostService;
import com.example.blog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    @Autowired
    private IPostService postService;

    @Autowired
    private IUserService userService;

    @GetMapping
    public ResponseEntity<List<PostApi>> getPosts(@RequestParam(defaultValue = "10") int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit must be a positive integer");
        }
        List<PostApi> posts = postService.getPosts(limit);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostApi> getPost(@PathVariable long id) {
        PostApi post = postService.getPost(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostApi> createPost(@RequestBody PostApi newPost) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        newPost.setCreator(((User) userService.loadUserByUsername(username)).toApi());
        PostApi createdPost = postService.createPost(newPost).toApi();
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void>  updatePost(@PathVariable long id, @RequestBody PostApi updatedPost) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        checkPermissions(id, username);

        updatedPost.setCreator(((User) userService.loadUserByUsername(username)).toApi());
        updatedPost.setId(id);
        postService.updatePost(updatedPost);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable long id)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        checkPermissions(id, username);

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    private void checkPermissions(long postId, String username) {
        boolean isAdmin = userService.loadUserByUsername(username).getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !postService.getPost(postId).getCreator().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not permitted to edit or delete other posts!");
        }
    }

}
