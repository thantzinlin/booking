package com.tzl.booking.service;

import com.tzl.booking.dto.AuthRequest;
import com.tzl.booking.entity.User;
import com.tzl.booking.exception.ResourceNotFoundException;
import com.tzl.booking.repository.UserInfoRepository;
import com.tzl.booking.utils.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;
    @Value("${resetpasswordlink}")
    private String resetPasswordLink;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = repository.findByEmail(username);

        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User addUser(User userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        return repository.save(userInfo);
    }

    public boolean verifyUser(String token) {
        User existingUser = repository.findByResetToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired verification token"));
        existingUser.setIsVerified(true);
        // existingUser.setResetToken("");
        repository.save(existingUser);
        return true;
    }

    public boolean checkPassword(User user, String currentPassword) {
        return encoder.matches(currentPassword, user.getPassword());
    }

    public void changePassword(User user, String newPassword) {
        String hashedPassword = encoder.encode(newPassword);
        user.setPassword(hashedPassword);
        repository.save(user);
    }

    public String forgotPassword(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateResetToken(user.getEmail());

        user.setResetToken(token);
        repository.save(user);

        String resetLink = resetPasswordLink + "?token=" + token;
        emailService.sendEmail(user.getEmail(), "Password Reset Request",
                "Click the link to reset your password: " + resetLink);
        return token;
    }

    public void resetPassword(String token) {
        String email = jwtService.validateResetToken(token);

        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newPassword = PasswordUtil.generateRandomPassword();
        user.setPassword(encoder.encode(newPassword));
        user.setResetToken("");
        repository.save(user);
        emailService.sendNewPasswordEmail(user.getEmail(), newPassword);

    }

    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

}