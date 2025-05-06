package com.tzl.booking.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tzl.booking.dto.BookClassRequest;
import com.tzl.booking.dto.CheckInRequest;
import com.tzl.booking.entity.Booking;
import com.tzl.booking.entity.ClassSchedule;
import com.tzl.booking.service.BookingService;
import com.tzl.booking.service.ClassScheduleService;
import com.tzl.booking.service.UserInfoDetails;
import com.tzl.booking.utils.CustomApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Module", description = "APIs for managing class bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

        @Autowired
        private BookingService bookingService;
        @Autowired
        private ClassScheduleService classScheduleService;

        @PostMapping("/book")
        @Operation(summary = "Book a class", description = "Allows authenticated users to book a class", responses = {
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<Map<String, Object>>> bookClass(
                        @RequestBody BookClassRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal UserInfoDetails userDetails) {

                Long userId = userDetails.getUserId();
                Booking booking = bookingService.bookClass(userId, request.getClassScheduleId());

                Map<String, Object> data = new HashMap<>();
                data.put("bookingId", booking.getId());

                CustomApiResponse<Map<String, Object>> response = CustomApiResponse.<Map<String, Object>>builder()
                                .data(data)
                                .build();

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @PostMapping("/cancel")
        @Operation(summary = "Cancel a booking", description = "Allows users to cancel their existing booking", responses = {
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<String>> cancelBooking(
                        CheckInRequest request,
                        @AuthenticationPrincipal UserInfoDetails userDetails) {

                Long userId = userDetails.getUserId();
                bookingService.cancelBooking(userId, request.getBookingId());

                CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                                .data("Successfully cancelled booking.")
                                .build();

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @PostMapping("/waitlist")
        @Operation(summary = "Join waitlist", description = "Add user to waitlist when class is full", responses = {
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<String>> addToWaitlist(
                        @RequestBody BookClassRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal UserInfoDetails userDetails) {

                Long userId = userDetails.getUserId();
                bookingService.addToWaitlist(userId, request.getClassScheduleId());

                CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                                .data("Successfully added to waitlist.")
                                .build();

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @PostMapping("/check-in")
        @Operation(summary = "Check-in for class", description = "Mark attendance for a booked class", responses = {

                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<String>> checkIn(
                        CheckInRequest request, @AuthenticationPrincipal UserInfoDetails userDetails) {

                Long userId = userDetails.getUserId();
                bookingService.checkIn(request.getBookingId(), userId);

                CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                                .data("Successfully checked in.")
                                .build();

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping("/class-schedules")
        @Operation(summary = "Get class schedules", description = "Fetches all available class schedules, optionally filtered by country", parameters = {
                        @Parameter(name = "country", description = "Filter classes by country name (e.g.,'Myanmar')", in = ParameterIn.QUERY, required = false, example = "Myanmar")
        })
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Request processed successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request, invalid data.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Data not found.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
        })
        public ResponseEntity<CustomApiResponse<List<ClassSchedule>>> getAvailableClassSchedules(
                        @RequestParam(required = false) String country) {
                List<ClassSchedule> classSchedules = null;
                if (country != null && !country.isEmpty()) {
                        classSchedules = classScheduleService.getClassSchedulesByCountry(country);
                } else {
                        classSchedules = classScheduleService.getAllClassSchedules();
                }

                CustomApiResponse<List<ClassSchedule>> response = CustomApiResponse.<List<ClassSchedule>>builder()
                                .data(classSchedules)
                                .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

}
