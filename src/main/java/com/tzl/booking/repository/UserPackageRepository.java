package com.tzl.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tzl.booking.entity.User;
import com.tzl.booking.entity.UserPackage;

public interface UserPackageRepository extends JpaRepository<UserPackage, Long> {
        List<UserPackage> findByUser(User user);

        boolean existsByUserAndPkgId(User user, Long pkgId);

        @Query("SELECT up FROM UserPackage up " +
                        "JOIN up.pkg p " + // Correct field name
                        "WHERE up.user = :user " +
                        "AND p.country = :country " +
                        "AND up.status = 'ACTIVE' " +
                        "AND up.remainingCredit > 0 " +
                        "AND p.expiryDate > CURRENT_TIMESTAMP " +
                        "ORDER BY p.expiryDate ASC")
        Optional<UserPackage> findActivePackageForCountry(
                        @Param("country") String country,
                        @Param("user") User user);

}
