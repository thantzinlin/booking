package com.tzl.booking.service;

import com.tzl.booking.entity.ClassSchedule;
import com.tzl.booking.exception.ResourceNotFoundException;
import com.tzl.booking.repository.ClassScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassScheduleService {

    private final ClassScheduleRepository classScheduleRepository;

    @Autowired
    public ClassScheduleService(ClassScheduleRepository classScheduleRepository) {
        this.classScheduleRepository = classScheduleRepository;
    }

    public List<ClassSchedule> getAllClassSchedules() {
        return classScheduleRepository.findAll();
    }

    public List<ClassSchedule> getClassSchedulesByCountry(String country) {
        List<ClassSchedule> schedules = classScheduleRepository.findByCountry(country);
        if (schedules.isEmpty()) {
            throw new ResourceNotFoundException("No class schedules found for country: " + country);
        }
        return schedules;
    }
}
