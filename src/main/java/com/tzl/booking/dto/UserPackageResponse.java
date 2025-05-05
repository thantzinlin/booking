package com.tzl.booking.dto;

import java.time.LocalDateTime;

import com.tzl.booking.enums.PackageStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPackageResponse {
    private Long id;
    private Long packageId;
    private String country;
    private String packageName;
    private int remainingCredits;
    private PackageStatus status;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiryDate;
}