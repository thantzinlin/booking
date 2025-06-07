package com.tzl.booking.controller;

import com.tzl.booking.dto.AuthRequest;
import com.tzl.booking.dto.ChangePasswordRequest;
import com.tzl.booking.dto.ForgotPasswordRequest;
import com.tzl.booking.dto.LoginRequest;
import com.tzl.booking.dto.UserProfileDto;
import com.tzl.booking.entity.User;
import com.tzl.booking.service.EmailService;
import com.tzl.booking.service.JwtService;
import com.tzl.booking.service.UserService;
import com.tzl.booking.utils.CustomApiResponse;
import com.tzl.booking.utils.ResponseConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<Map<String, Object>>> addNewUser(
                        @RequestBody @Valid AuthRequest request) {
                String token = UUID.randomUUID().toString();
                User userInfo = new User();
                userInfo.setResetToken(token);
                userInfo.setIsVerified(false);
                userInfo.setEmail(request.getEmail());
                userInfo.setPassword(request.getPassword());
                userInfo.setName(request.getUsername());
                userInfo.setPhoneNumber(request.getPhoneNumber());
                userInfo.setAddress(request.getAddress());
                service.addUser(userInfo);

                emailService.sendVerifyEmail(userInfo.getEmail(), token);
                Map<String, Object> data = new HashMap<>();
                data.put("verifytoken", token);
                CustomApiResponse<Map<String, Object>> response = CustomApiResponse.<Map<String, Object>>builder()
                                .returnMessage("Registration successful. Check your email to verify your account.")
                                .data(data).build();
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @PostMapping("/login")

        @Operation(summary = "Authenticate user", description = "Authenticates user and returns a JWT token.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid Credential.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })

        public ResponseEntity<CustomApiResponse<String>> authenticateAndGetToken(
                        @Valid @RequestBody LoginRequest authRequest) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
                                                authRequest.getPassword()));

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

        @GetMapping("/verify")
        @Operation(summary = "Verify user", description = "Verifies the user using the provided token.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<String>> verifyUser(@RequestParam("token") String token) {
                boolean result = service.verifyUser(token);
                if (!result) {
                        throw new BadCredentialsException("Invalid or expired verification token");
                }
                CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                                .data("User verified successfully").build();
                return new ResponseEntity<>(response, HttpStatus.OK);

        }

        @PostMapping("/forgot-password")
        @Operation(summary = "Forgot password", description = "Triggers forgot password logic (e.g. email or SMS reset code).")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
                String resetToken = service.forgotPassword(request.getEmail());
                CustomApiResponse<String> response = CustomApiResponse.<String>builder().build();
                response.setData(resetToken);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping("/reset-password")
        @Operation(summary = "Reset password", description = "Allows users to reset their password.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<String>> resetPassword(@RequestParam("token") String token) {
                String newPassword = service.resetPassword(token);
                CustomApiResponse<String> response = CustomApiResponse.<String>builder().build();
                response.setData(newPassword);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @PostMapping("/change-password")
        @Operation(summary = "Change password", description = "Allows users to change their password.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<String>> changePassword(
                        @RequestBody @Valid ChangePasswordRequest changePasswordRequest,
                        Authentication authentication) {
                if (changePasswordRequest.getCurrentPassword().equals(changePasswordRequest.getNewPassword())) {
                        throw new IllegalArgumentException("New password cannot be the same as current password");
                }
                if (changePasswordRequest.getCurrentPassword().length() < 8
                                || changePasswordRequest.getNewPassword().length() < 8) {
                        throw new IllegalArgumentException("Password must be at least 8 characters long");
                }
                User currentUser = service.findByEmail(authentication.getName());

                boolean isPasswordCorrect = service.checkPassword(currentUser,
                                changePasswordRequest.getCurrentPassword());
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
        @Operation(summary = "Get User Profile", description = "Allows to get user profile data.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<UserProfileDto>> getProfile(Authentication authentication) {
                String email = authentication.getName();
                User user = service.findByEmail(email);

                UserProfileDto profile = new UserProfileDto(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getPhoneNumber(),
                                user.getAddress(),
                                user.getIsVerified());

                CustomApiResponse<UserProfileDto> response = CustomApiResponse.<UserProfileDto>builder()
                                .data(profile)
                                .returnCode(ResponseConstants.SUCCESS_CODE)
                                .returnMessage(ResponseConstants.SUCCESS_MESSAGE)
                                .build();

                return ResponseEntity.ok(response);
        }
}
