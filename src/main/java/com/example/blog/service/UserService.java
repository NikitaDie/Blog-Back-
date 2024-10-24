package com.example.blog.service;

import com.example.blog.exception.InvalidDataException;
import com.example.blog.exception.NotAvailableException;
import com.example.blog.exception.NotPresentedException;
import com.example.blog.exception.WrongPasswordException;
import com.example.blog.model.User;
import com.example.blog.model.UserApi;
import com.example.blog.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepo userRepo;

    public User findUserByLogin(String login) {
        return userRepo.findByLogin(login).orElseThrow(
                () -> new NotPresentedException("There is not user with such login: " + login)
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(
                () -> new NotPresentedException("There is not user with such username: " + username)
        );
    }

    @Override
    public UserApi login(String login, String password) {
        User user = findUserByLogin(login);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (encoder.matches(password, user.getPassword())) {
            return user.toApi();
        } else {
            throw new WrongPasswordException("Wrong password");
        }
    }

    @Override
    public UserApi create(UserApi newUser) {
        validateUserData(newUser);

        try {
            findUserByLogin(newUser.getUsername());
        } catch (NotPresentedException e) {
            User user = User.builder()
                    .username(newUser.getUsername())
                    .password(new BCryptPasswordEncoder().encode(newUser.getPassword()))
                    .authorities("ROLE_USER")
                    .build();
            userRepo.save(user);
            return user.toApi();
        }
        throw new NotAvailableException("The user with such username already exists");
    }

    private void validateUserData(UserApi newUser) {
        if (newUser.getUsername() == null || newUser.getUsername().length() < 3) {
            throw new InvalidDataException("Username must be at least 3 characters long");
        }

        if (newUser.getPassword() == null || !isValidPassword(newUser.getPassword())) {
            throw new InvalidDataException("Password must be at least 8 characters long, contain at least one uppercase letter, one number, and one special character");
        }

        if (newUser.getLogin() == null || !isValidEmail(newUser.getLogin())) {
            throw new InvalidDataException("Invalid email address");
        }
    }

    private boolean isValidPassword(String password) {
        int minLength = 8;
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasNumber = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*(),.?\":{}|<>".indexOf(ch) >= 0);
        return password.length() >= minLength && hasUpperCase && hasNumber && hasSpecialChar;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        return email.matches(emailRegex);
    }

    @Override
    public UserApi findUserById(long id) {
        return userRepo.findById(id).orElseThrow(
                () -> new NotPresentedException("There is not user with such id: " + id)
        ).toApi();
    }
}
