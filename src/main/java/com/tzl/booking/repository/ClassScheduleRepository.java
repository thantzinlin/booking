package com.tzl.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tzl.booking.entity.ClassSchedule;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
        List<ClassSchedule> findByCountryAndStartTimeAfter(String country, LocalDateTime startTime);

        List<ClassSchedule> findByCountry(String country);

        List<ClassSchedule> findByEndTimeBefore(LocalDateTime time);

        @Query("SELECT cs FROM ClassSchedule cs WHERE cs.country = :country " +
                        "AND cs.startTime BETWEEN :start AND :end " +
                        "ORDER BY cs.startTime ASC")
        List<ClassSchedule> findUpcomingClasses(
                        @Param("country") String country,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

}
