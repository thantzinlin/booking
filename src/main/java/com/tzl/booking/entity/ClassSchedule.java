package com.tzl.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "class_schedule")
public class ClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Class name cannot be empty")
    @Size(max = 100, message = "Class name cannot exceed 100 characters")
    @Column(nullable = false)
    private String className;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Future(message = "Start time must be in the future")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Future(message = "End time must be in the future")
    @Column(nullable = false)
    private LocalDateTime endTime;

    @NotBlank(message = "Country cannot be empty")
    @Size(max = 100, message = "Country name cannot exceed 100 characters")
    @Column(nullable = false)
    private String country;

    @Min(value = 1, message = "Maximum capacity must be at least 1")
    @Column(nullable = false)
    private Integer maxCapacity;

    @Builder.Default
    @Column(nullable = false)
    private Integer bookedCount = 0;

    @Min(value = 1, message = "Required credit must be at least 1")
    @Column(nullable = false)
    private Integer requiredCredit;

    @NotBlank(message = "Instructor name cannot be empty")
    @Size(max = 100, message = "Instructor name cannot exceed 100 characters")
    private String instructorName;

    @NotBlank(message = "Location cannot be empty")
    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Business logic methods
    public void incrementBookedCount() {
        if (this.bookedCount >= this.maxCapacity) {
            throw new IllegalStateException("Class is already at maximum capacity");
        }
        this.bookedCount++;
    }

    public void decrementBookedCount() {
        if (this.bookedCount <= 0) {
            throw new IllegalStateException("No bookings to decrement");
        }
        this.bookedCount--;
    }

    // Validation method
    public void validateTiming() {
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }
}