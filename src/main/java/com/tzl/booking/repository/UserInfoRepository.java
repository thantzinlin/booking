package com.tzl.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tzl.booking.entity.User;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    Optional<User> findByResetToken(String token);
}
