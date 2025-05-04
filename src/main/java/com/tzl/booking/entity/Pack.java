package com.tzl.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "packages")
public class Pack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Package name cannot be empty")
    private String name;

    @NotBlank(message = "Country cannot be empty")
    private String country;

    @Min(value = 1, message = "Credit must be at least 1")
    private Integer credit;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean isActive = true;

    private Date purchasedDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
