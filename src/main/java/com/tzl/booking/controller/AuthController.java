package com.tzl.booking.controller;

import com.tzl.booking.dto.AuthRequest;
import com.tzl.booking.entity.User;
import com.tzl.booking.service.EmailService;
import com.tzl.booking.service.JwtService;
import com.tzl.booking.service.UserService;
import com.tzl.booking.utils.ApiResponse;

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
@Tag(name = "Authentication API", description = "Endpoints for user registration, login, and profile access")
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
    public ResponseEntity<ApiResponse<Map<String, Object>>> addNewUser(@RequestBody @Valid User userInfo) {
        String token = UUID.randomUUID().toString();
        userInfo.setResetToken(token);
        userInfo.setIsVerified(false);
        service.addUser(userInfo);

        emailService.sendVerifyEmail(userInfo.getEmail(), token);
        Map<String, Object> data = new HashMap<>();
        data.put("verifytoken", token);
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .returnMessage("Registration successful. Check your email to verify your account.")
                .data(data).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyUser(@RequestParam("token") String token) {
        boolean result = service.verifyUser(token);
        if (!result) {
            throw new BadCredentialsException("Invalid or expired verification token");
        }
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data("User verified successfully").build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user and returns a JWT token.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authentication successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<ApiResponse<String>> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
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
        ApiResponse<String> response = ApiResponse.<String>builder().data(token).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Triggers forgot password logic (e.g. email or SMS reset code).")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody AuthRequest request) {
        service.forgotPassword(request);
        ApiResponse<String> response = ApiResponse.<String>builder().build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Allows users to reset their password.")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam("token") String token) {
        service.resetPassword(token);
        ApiResponse<String> response = ApiResponse.<String>builder().build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
