package com.tzl.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tzl.booking.enums.BookingStatus;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_schedule_id", nullable = false)
    private ClassSchedule classSchedule;

    @ManyToOne
    @JoinColumn(name = "user_package_id", nullable = false)
    private UserPackage userPackage;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.BOOKED;

    private boolean checkedIn = false;

    private LocalDateTime checkInTime;

    @CreationTimestamp
    private LocalDateTime bookingTime;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
