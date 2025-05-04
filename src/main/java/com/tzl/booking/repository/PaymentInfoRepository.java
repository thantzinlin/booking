package com.tzl.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tzl.booking.entity.PaymentInfo;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {

}
