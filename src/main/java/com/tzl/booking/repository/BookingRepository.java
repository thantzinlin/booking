package com.tzl.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tzl.booking.entity.Booking;
import com.tzl.booking.entity.ClassSchedule;
import com.tzl.booking.entity.User;
import com.tzl.booking.enums.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);

    Optional<Booking> findByIdAndUserId(Long bookingId, Long userId);

    List<Booking> findByClassSchedule(ClassSchedule classSchedule);

    List<Booking> findByUserAndStatus(User user, BookingStatus status);

    boolean existsByUserAndClassSchedule(User user, ClassSchedule classSchedule);

    Optional<Booking> findByUserAndClassSchedule(User user, ClassSchedule classSchedule);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.classSchedule = :classSchedule AND b.status = 'BOOKED'")
    int countActiveBookingsForClass(@Param("classSchedule") ClassSchedule classSchedule);

}
