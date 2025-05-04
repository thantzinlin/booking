package com.tzl.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_info")
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private BigDecimal amount;

    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String paymentMethod;

    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "user_package_id")
    private UserPackage userPackage;
}
