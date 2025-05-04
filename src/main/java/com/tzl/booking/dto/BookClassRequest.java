package com.tzl.booking.dto;

import lombok.Data;

@Data
public class BookClassRequest {
    private Long classScheduleId;
    private Long userPackageId;
    private Long bookingId;

}