package com.tzl.booking.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tzl.booking.dto.BookClassRequest;
import com.tzl.booking.entity.Booking;
import com.tzl.booking.service.BookingService;
import com.tzl.booking.service.UserInfoDetails;
import com.tzl.booking.utils.ApiResponse;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bookClass(@RequestBody BookClassRequest request,
            @AuthenticationPrincipal UserInfoDetails userDetails) {
        Long userId = userDetails.getUserId();
        Booking booking = bookingService.bookClass(userId, request.getClassScheduleId());
        Map<String, Object> data = new HashMap<>();
        data.put("bookingId", booking.getId());
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .data(data)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancelBooking(@RequestBody BookClassRequest request,
            @AuthenticationPrincipal UserInfoDetails userDetails) {
        Long userId = userDetails.getUserId();

        bookingService.cancelBooking(userId, request.getBookingId());
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data("Successfully cancelled booking.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/waitlist")
    public ResponseEntity<ApiResponse<String>> addToWaitlist(@RequestBody BookClassRequest request,
            @AuthenticationPrincipal UserInfoDetails userDetails) {
        Long userId = userDetails.getUserId();
        bookingService.addToWaitlist(userId, request.getClassScheduleId());
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data("Successfully added to waitlist.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping("/check-in")
    public ResponseEntity<ApiResponse<String>> checkIn(@RequestParam Long bookingId,
            @AuthenticationPrincipal UserInfoDetails userDetails) {
        Long userId = userDetails.getUserId();

        bookingService.checkIn(bookingId, userId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data("Successfully checked in.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
