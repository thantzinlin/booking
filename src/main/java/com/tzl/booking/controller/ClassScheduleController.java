package com.tzl.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tzl.booking.entity.ClassSchedule;
import com.tzl.booking.service.ClassScheduleService;
import com.tzl.booking.utils.ApiResponse;

@RestController
@RequestMapping("/api/class-schedule")
public class ClassScheduleController {
    private final ClassScheduleService classScheduleService;

    @Autowired
    public ClassScheduleController(ClassScheduleService classScheduleService) {
        this.classScheduleService = classScheduleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassSchedule>>> getAvailableClassSchedules(
            @RequestParam(required = false) String country) {
        List<ClassSchedule> classSchedules = null;
        if (country != null && !country.isEmpty()) {
            classSchedules = classScheduleService.getClassSchedulesByCountry(country);
        } else {
            classSchedules = classScheduleService.getAllClassSchedules();
        }

        ApiResponse<List<ClassSchedule>> response = ApiResponse.<List<ClassSchedule>>builder()
                .data(classSchedules)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
