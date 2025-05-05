package com.tzl.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tzl.booking.enums.PackageStatus;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_packages")
public class UserPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "package_id")
    private Pack pkg;

    @Min(value = 0, message = "Remaining credit cannot be negative")
    private Integer remainingCredit;

    @Enumerated(EnumType.STRING)
    private PackageStatus status;

    private LocalDateTime purchasedDate;

    private LocalDateTime expiryDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
