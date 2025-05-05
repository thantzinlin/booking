package com.tzl.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tzl.booking.entity.Pack;

public interface PackRepository extends JpaRepository<Pack, Long> {
    List<Pack> findByCountry(String country);

    Optional<Pack> findByIdAndIsActiveTrue(Long id);

}
