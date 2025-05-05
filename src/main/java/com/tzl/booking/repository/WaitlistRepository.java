package com.tzl.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tzl.booking.entity.ClassSchedule;
import com.tzl.booking.entity.User;
import com.tzl.booking.entity.Waitlist;

public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    List<Waitlist> findByScheduleOrderByJoinedAtAsc(ClassSchedule schedule);

    List<Waitlist> findByRefundedFalse();

    boolean existsByUserAndSchedule(User user, ClassSchedule schedule);

}
