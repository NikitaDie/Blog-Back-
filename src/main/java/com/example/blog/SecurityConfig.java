package com.example.blog;

import com.example.blog.utils.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest

                        // Require authentication for the following endpoints
                        .requestMatchers(HttpMethod.POST,"/api/v1/posts/").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/posts/**").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/posts/**").hasAuthority("ROLE_USER")

                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
