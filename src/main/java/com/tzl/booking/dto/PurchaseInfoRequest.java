package com.tzl.booking.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PurchaseInfoRequest {
    private Long packageId;
    private BigDecimal amount;
    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String paymentMethod;
}