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
                        @Parameter(name = "country", description = "Country code to filter packages (e.g., 'Myanmar')", required = true, in = ParameterIn.QUERY, example = "Myanmar")
        }, responses = {
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<List<PackageResponse>>> getAvailablePackagesByCountry(
                        @RequestParam String country,
                        @Parameter(hidden = true) Authentication authentication) throws Exception {
                String email = authentication.getName();
                List<PackageResponse> packages = packageService.getAvailablePackagesByCountry(country, email);
                CustomApiResponse<List<PackageResponse>> response = CustomApiResponse.<List<PackageResponse>>builder()
                                .data(packages)
                                .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping("/my-packages")
        @Operation(summary = "Get user's purchased packages", description = "Retrieve all packages purchased by the authenticated user", responses = {
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<List<UserPackageResponse>>> getMyPackage(
                        @Parameter(hidden = true) Authentication authentication) {
                String email = authentication.getName();
                List<UserPackageResponse> data = packageService.getMyPackage(email);

                CustomApiResponse<List<UserPackageResponse>> response = CustomApiResponse
                                .<List<UserPackageResponse>>builder()
                                .data(data)
                                .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @PostMapping("/purchase")
        @Operation(summary = "Purchase a package", description = "Purchase a new package for the authenticated user", responses = {
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<String>> purchase(
                        @RequestBody PurchaseInfoRequest purchaseInfoRequest,
                        @Parameter(hidden = true) Authentication authentication) {
                String email = authentication.getName();
                packageService.purchasePackage(email, purchaseInfoRequest);
                CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                                .data("Successfully purchased package.")
                                .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
        }
}