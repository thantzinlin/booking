package com.tzl.booking.controller;

import com.tzl.booking.dto.AuthRequest;
import com.tzl.booking.dto.ChangePasswordRequest;
import com.tzl.booking.dto.UserProfileDto;
import com.tzl.booking.entity.User;
import com.tzl.booking.service.EmailService;
import com.tzl.booking.service.JwtService;
import com.tzl.booking.service.UserService;
import com.tzl.booking.utils.CustomApiResponse;
import com.tzl.booking.utils.ResponseConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "User Module", description = "Endpoints for user registration, login, and profile access")
public class AuthController {

    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Registers a new user in the system.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<CustomApiResponse<Map<String, Object>>> addNewUser(@RequestBody @Valid User userInfo) {
        String token = UUID.randomUUID().toString();
        userInfo.setResetToken(token);
        userInfo.setIsVerified(false);
        service.addUser(userInfo);

        emailService.sendVerifyEmail(userInfo.getEmail(), token);
        Map<String, Object> data = new HashMap<>();
        data.put("verifytoken", token);
        CustomApiResponse<Map<String, Object>> response = CustomApiResponse.<Map<String, Object>>builder()
                .returnMessage("Registration successful. Check your email to verify your account.")
                .data(data).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<CustomApiResponse<String>> verifyUser(@RequestParam("token") String token) {
        boolean result = service.verifyUser(token);
        if (!result) {
            throw new BadCredentialsException("Invalid or expired verification token");
        }
        CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                .data("User verified successfully").build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user and returns a JWT token.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authentication successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<CustomApiResponse<String>> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = service.findByEmail(authRequest.getEmail());
        if (!user.getIsVerified()) {
            throw new BadCredentialsException("Please verify your email before logging in.");
        }

        String token = jwtService.generateToken(authRequest.getEmail());
        CustomApiResponse<String> response = CustomApiResponse.<String>builder().data(token).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Triggers forgot password logic (e.g. email or SMS reset code).")
    public ResponseEntity<CustomApiResponse<String>> forgotPassword(@RequestBody AuthRequest request) {
        service.forgotPassword(request);
        CustomApiResponse<String> response = CustomApiResponse.<String>builder().build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Allows users to reset their password.")
    public ResponseEntity<CustomApiResponse<String>> resetPassword(@RequestParam("token") String token) {
        service.resetPassword(token);
        CustomApiResponse<String> response = CustomApiResponse.<String>builder().build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Allows users to change their password.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or current password is incorrect"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<CustomApiResponse<String>> changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        if (changePasswordRequest.getCurrentPassword().equals(changePasswordRequest.getNewPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as current password");
        }
        if (changePasswordRequest.getCurrentPassword().length() < 8
                || changePasswordRequest.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        User currentUser = service.findByEmail(authentication.getName());

        boolean isPasswordCorrect = service.checkPassword(currentUser, changePasswordRequest.getCurrentPassword());
        if (!isPasswordCorrect) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        service.changePassword(currentUser, changePasswordRequest.getNewPassword());

        CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                .data("Password changed successfully")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomApiResponse<UserProfileDto>> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = service.findByEmail(email);

        UserProfileDto profile = new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIsVerified());

        CustomApiResponse<UserProfileDto> response = CustomApiResponse.<UserProfileDto>builder()
                .data(profile)
                .returnCode(ResponseConstants.SUCCESS_CODE)
                .returnMessage(ResponseConstants.SUCCESS_MESSAGE)
                .build();

        return ResponseEntity.ok(response);
    }
}
