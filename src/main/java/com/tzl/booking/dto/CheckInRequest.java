package com.tzl.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckInRequest {
    @NotBlank(message = "Class schedule ID is required")
    private Long bookingId;

}