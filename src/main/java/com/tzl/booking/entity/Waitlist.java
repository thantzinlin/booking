package com.tzl.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "waitlist")
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private ClassSchedule schedule;

    @ManyToOne(optional = false)
    private UserPackage userPackage;

    private LocalDateTime joinedAt = LocalDateTime.now();

    private boolean refunded = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
