package com.example.blog.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "USERS")
public class User implements UserDetails {
    private static final String AUTHORITIES_DELIMITER = "::";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotNull
    @Column(unique = true)
    private String username;

    @Setter
    @NotNull
    private String password;

    @NotNull
    private String authorities;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Post> posts;

    protected User() {}

    // Constructor to create a User from UserApi
    public User(UserApi userApi) {
        this.id = userApi.getId();
        this.username = userApi.getUsername();
        this.password = userApi.getPassword();
        this.authorities = userApi.getAuthorities();
    }

    // Convert to UserApi
    public UserApi toApi() {
        return new UserApi(id, username, password, posts, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(this.authorities.split(AUTHORITIES_DELIMITER))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Change to your actual logic if necessary
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Change to your actual logic if necessary
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Change to your actual logic if necessary
    }

    @Override
    public boolean isEnabled() {
        return true; // Change to your actual logic if necessary
    }
}
