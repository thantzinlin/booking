package com.tzl.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.tzl.booking.service.PackService;
import com.tzl.booking.utils.ApiResponse;
import com.tzl.booking.dto.PackageResponse;
import com.tzl.booking.dto.PurchaseInfoRequest;
import com.tzl.booking.dto.UserPackageResponse;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private PackService packageService;

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getAvailablePackagesByCountry(
            @RequestParam String country,
            Authentication authentication) throws Exception {
        String email = authentication.getName();
        List<PackageResponse> packages = packageService.getAvailablePackagesByCountry(country, email);
        ApiResponse<List<PackageResponse>> response = ApiResponse.<List<PackageResponse>>builder()
                .data(packages)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/my-packages")
    public ResponseEntity<ApiResponse<List<UserPackageResponse>>> getMyPackage(Authentication authentication) {
        String email = authentication.getName();
        List<UserPackageResponse> data = packageService.getMyPackage(email);

        ApiResponse<List<UserPackageResponse>> response = ApiResponse.<List<UserPackageResponse>>builder()
                .data(data)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<String>> purchase(@RequestBody PurchaseInfoRequest purchaseInfoRequest,
            Authentication authentication) {
        String email = authentication.getName();
        packageService.purchasePackage(email, purchaseInfoRequest);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data("Successfully purchased package.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}