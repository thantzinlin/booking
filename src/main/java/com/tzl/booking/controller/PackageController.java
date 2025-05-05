package com.tzl.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.tzl.booking.service.PackService;
import com.tzl.booking.utils.CustomApiResponse;
import com.tzl.booking.dto.PackageResponse;
import com.tzl.booking.dto.PurchaseInfoRequest;
import com.tzl.booking.dto.UserPackageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/packages")
@Tag(name = "Package Module", description = "APIs for managing user packages")
@SecurityRequirement(name = "bearerAuth")
public class PackageController {

    @Autowired
    private PackService packageService;

    @GetMapping("/available")
    @Operation(summary = "Get available packages by country", description = "Retrieve packages available for purchase in a specific country", parameters = {
            @Parameter(name = "country", description = "Country code to filter packages (e.g., 'US', 'MM')", required = true, in = ParameterIn.QUERY, example = "MM")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved packages", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid country parameter"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<CustomApiResponse<List<PackageResponse>>> getAvailablePackagesByCountry(
            @RequestParam String country,
            @Parameter(hidden = true) Authentication authentication) throws Exception {
        // Existing implementation remains unchanged
        String email = authentication.getName();
        List<PackageResponse> packages = packageService.getAvailablePackagesByCountry(country, email);
        CustomApiResponse<List<PackageResponse>> response = CustomApiResponse.<List<PackageResponse>>builder()
                .data(packages)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/my-packages")
    @Operation(summary = "Get user's purchased packages", description = "Retrieve all packages purchased by the authenticated user", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user packages", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<CustomApiResponse<List<UserPackageResponse>>> getMyPackage(
            @Parameter(hidden = true) Authentication authentication) {
        // Existing implementation remains unchanged
        String email = authentication.getName();
        List<UserPackageResponse> data = packageService.getMyPackage(email);

        CustomApiResponse<List<UserPackageResponse>> response = CustomApiResponse.<List<UserPackageResponse>>builder()
                .data(data)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/purchase")
    @Operation(summary = "Purchase a package", description = "Purchase a new package for the authenticated user", responses = {
            @ApiResponse(responseCode = "200", description = "Package purchased successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid purchase request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<CustomApiResponse<String>> purchase(
            @RequestBody PurchaseInfoRequest purchaseInfoRequest,
            @Parameter(hidden = true) Authentication authentication) {
        // Existing implementation remains unchanged
        String email = authentication.getName();
        packageService.purchasePackage(email, purchaseInfoRequest);
        CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                .data("Successfully purchased package.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}