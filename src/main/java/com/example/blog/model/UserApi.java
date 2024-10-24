package com.example.blog.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class UserApi {
    private Long id;
    private String username;
    private String login;
    private String password;
    private List<Post> posts;
    private String authorities;

    public UserApi(Long id, String username, String login, String password, List<Post> posts, String authorities) {
        this.id = id;
        this.username = username;
        this.login = login;
        this.password = password;
        this.posts = posts;
        this.authorities = authorities;
    }
}
