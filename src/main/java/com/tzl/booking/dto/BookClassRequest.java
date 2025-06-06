package com.tzl.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookClassRequest {
    @NotBlank(message = "Class schedule ID is required")
    private Long classScheduleId;

}