package com.example.blog.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

@Getter
@Entity
@Table(name = "POSTS")
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @NotNull
    private String title;

    @Setter
    @Column(length = 100000)
    private String content;

    @Setter
    @NotNull
    private Timestamp created;

    @Setter
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    @JsonBackReference
    private User creator;

    protected Post() {}

    public Post (PostApi postApi)
    {
        this.id = postApi.getId();
        this.title = postApi.getTitle();
        this.content = postApi.getContent();
        this.created = postApi.getCreated() == null ? new Timestamp(System.currentTimeMillis()) : postApi.getCreated();
        this.creator = new User(postApi.getCreator());
    }

    public PostApi toApi()
    {
        return new PostApi(id, title, content, created, creator.toApi());
    }
}
