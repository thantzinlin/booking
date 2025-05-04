package com.tzl.booking.controller;

import com.tzl.booking.dto.ChangePasswordRequest;
import com.tzl.booking.dto.UserProfileDto;
import com.tzl.booking.entity.User;
import com.tzl.booking.service.UserService;
import com.tzl.booking.utils.ApiResponse;
import com.tzl.booking.utils.ResponseConstants;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Allows users to change their password.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or current password is incorrect"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<ApiResponse<String>> changePassword(
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

        ApiResponse<String> response = ApiResponse.<String>builder()
                .data("Password changed successfully")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = service.findByEmail(email);

        UserProfileDto profile = new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIsVerified());

        ApiResponse<UserProfileDto> response = ApiResponse.<UserProfileDto>builder()
                .data(profile)
                .returnCode(ResponseConstants.SUCCESS_CODE)
                .returnMessage(ResponseConstants.SUCCESS_MESSAGE)
                .build();

        return ResponseEntity.ok(response);
    }

}
