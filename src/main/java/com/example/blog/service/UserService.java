package com.example.blog.service;

import com.example.blog.exception.*;
import com.example.blog.model.User;
import com.example.blog.model.UserApi;
import com.example.blog.repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JavaMailSender mailSender;


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
            if (!user.isEnabled())
                throw new UnauthorizedException("The user is not activated. Check your email for activation letter.");

            return user.toApi();
        } else {
            throw new WrongPasswordException("Wrong password");
        }
    }

    @Override
    public UserApi create(UserApi newUser, String siteURL) {
        validateUserData(newUser);

        if (userExists(newUser.getLogin(), newUser.getUsername())) {
            throw new NotAvailableException("The user with such username already exists");
        }

        User user = User.builder()
                .username(newUser.getUsername())
                .login(newUser.getLogin())
                .password(new BCryptPasswordEncoder().encode(newUser.getPassword()))
                .authorities("ROLE_USER")
                .verificationCode(RandomStringUtils.randomAlphanumeric(64))
                .enabled(false)
                .build();
        userRepo.save(user);

        try {
            sendVerificationEmail(user, siteURL);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        return user.toApi();
    }

    private boolean userExists(String login, String username) {
        boolean exists = false;
        try {
            findUserByLogin(login);
            exists = true;
        } catch (NotPresentedException _) {}
        try {
            loadUserByUsername(username);
            exists = true;
        } catch (NotPresentedException _) {}

        return exists;
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

    public boolean verify(String verificationCode) {
        User user = userRepo.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepo.save(user);

            return true;
        }

    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getLogin();
        String fromAddress = "nik0609tak@gmail.com";
        String senderName = "NikitaDeveloper";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "NikitaDeveloper";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = siteURL + "/api/v1/users/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }
}
