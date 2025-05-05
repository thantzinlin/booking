package com.tzl.booking.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PackageResponse {
    private Long id;
    private String name;
    private int credits;
    private BigDecimal price;
    private String country;
    private Date expiryDate;

    public PackageResponse(Long id, String name, int credits, BigDecimal price, String country, Date expiryDate) {
        this.id = id;
        this.name = name;
        this.credits = credits;
        this.price = price;
        this.country = country;
        this.expiryDate = expiryDate;
    }

}