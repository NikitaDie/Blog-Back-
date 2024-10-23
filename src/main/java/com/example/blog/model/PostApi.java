package com.example.blog.model;

import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class PostApi {

    private Long id;
    private String title;
    private String content;
    private Timestamp created;
    private UserApi creator;

    public PostApi(Long id, String title, String content, Timestamp created, UserApi creator) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.created = created;
        this.creator = creator;
    }

}
