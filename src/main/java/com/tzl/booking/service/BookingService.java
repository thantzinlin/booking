package com.tzl.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tzl.booking.entity.*;
import com.tzl.booking.enums.BookingStatus;
import com.tzl.booking.exception.*;
import com.tzl.booking.repository.*;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ClassScheduleRepository classScheduleRepository;
    @Autowired
    private UserPackageRepository userPackageRepository;
    @Autowired
    private WaitlistRepository waitlistRepository;
    @Autowired
    private UserInfoRepository userRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Transactional
    public Booking bookClass(Long userId, Long classId) {

        String lockKey = "class_booking:" + classId;
        boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 10, TimeUnit.SECONDS);

        if (!locked) {
            throw new ConcurrentBookingException("Another booking is in progress for this class");
        }

        try {
            User user = userRepository.findById(userId.intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            ClassSchedule classSchedule = classScheduleRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

            if (bookingRepository.existsByUserAndClassSchedule(user, classSchedule)) {
                throw new BusinessRuleException("You have already booked this class");
            }

            checkForOverlappingBookings(user, classSchedule);

            UserPackage userPackage = userPackageRepository
                    .findActivePackageForCountry(classSchedule.getCountry(), user)
                    .orElseThrow(() -> new BusinessRuleException(
                            "Your current package is not valid for this class because the country does not match."));
            if (classSchedule.getStartTime().isBefore(LocalDateTime.now())) {
                throw new BusinessRuleException("Cannot book past classes");
            }
            if (classSchedule.getBookedCount() >= classSchedule.getMaxCapacity()) {
                throw new BusinessRuleException("Class is full. You can join the waitlist if available");
            }
            if (userPackage.getRemainingCredit() < classSchedule.getRequiredCredit()) {
                throw new BusinessRuleException("Insufficient credits");
            }

            userPackage.setRemainingCredit(userPackage.getRemainingCredit() - classSchedule.getRequiredCredit());
            userPackageRepository.save(userPackage);

            Booking booking = new Booking();
            booking.setUser(user);
            booking.setClassSchedule(classSchedule);
            booking.setUserPackage(userPackage);
            booking.setStatus(BookingStatus.BOOKED);

            classSchedule.incrementBookedCount();
            classScheduleRepository.save(classSchedule);

            Booking res = bookingRepository.save(booking);
            return res;
        } catch (Exception e) {
            log.error("Booking failed with: {}", e.getMessage());
            throw e;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Transactional
    public void cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found for this user"));

        ClassSchedule classSchedule = booking.getClassSchedule();
        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new BusinessRuleException("Only booked classes can be cancelled");
        }
        if (booking.isCheckedIn()) {
            throw new BusinessRuleException("You cannot cancel a class after checking in");
        }
        UserPackage userPackage = booking.getUserPackage();

        if (LocalDateTime.now().isBefore(classSchedule.getStartTime().minusHours(4))) {
            // Refund credit
            userPackage.setRemainingCredit(userPackage.getRemainingCredit() + classSchedule.getRequiredCredit());
            userPackageRepository.save(userPackage);
        } else {
            log.info("No refund: cancellation was within 4 hours of class start.");
        }

        classSchedule.decrementBookedCount();
        classScheduleRepository.save(classSchedule);

        ClassSchedule schedule = booking.getClassSchedule();
        schedule.setBookedCount(schedule.getBookedCount() - 1);
        classScheduleRepository.save(schedule);

        // Cancel the booking
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Check if someone is on waitlist
        List<Waitlist> waitlist = waitlistRepository.findByScheduleOrderByJoinedAtAsc(schedule);
        if (!waitlist.isEmpty()) {
            Waitlist next = waitlist.get(0);
            waitlistRepository.delete(next);

            // Create a new booking for waitlist user
            Booking newBooking = new Booking();
            newBooking.setUser(next.getUser());
            newBooking.setClassSchedule(schedule);
            newBooking.setUserPackage(next.getUserPackage());
            newBooking.setStatus(BookingStatus.BOOKED);
            newBooking.setCheckedIn(false);

            schedule.setBookedCount(schedule.getBookedCount() + 1);
            classScheduleRepository.save(schedule);

            bookingRepository.save(newBooking);
        }
    }

    @Transactional
    public void checkIn(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found for this user"));

        if (booking.isCheckedIn()) {
            throw new BusinessRuleException("You have already checked in.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(booking.getClassSchedule().getStartTime())) {
            throw new BusinessRuleException("Check-in is only allowed when the class starts.");
        }

        booking.setCheckedIn(true);
        booking.setCheckInTime(now);
        bookingRepository.save(booking);
    }

    private void checkForOverlappingBookings(User user, ClassSchedule newClass) {
        List<Booking> existingBookings = bookingRepository.findByUserAndStatus(user, BookingStatus.BOOKED);
        for (Booking existing : existingBookings) {
            if (isOverlapping(existing.getClassSchedule(), newClass)) {
                throw new BusinessRuleException(
                        String.format("You already have a booking for %s that overlaps with this class",
                                existing.getClassSchedule().getClassName()));
            }
        }
    }

    private boolean isOverlapping(ClassSchedule a, ClassSchedule b) {
        return a.getStartTime().isBefore(b.getEndTime()) && a.getEndTime().isAfter(b.getStartTime());
    }

    @Transactional
    public void addToWaitlist(Long userId, Long classScheduleId) {
        User user = userRepository.findById(userId.intValue())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Class schedule not found"));

        if (waitlistRepository.existsByUserAndSchedule(user, classSchedule)) {
            throw new BusinessRuleException("You are already in the waitlist for this class");
        }

        UserPackage userPackage = userPackageRepository
                .findActivePackageForCountry(classSchedule.getCountry(), user)
                .orElseThrow(() -> new BusinessRuleException(
                        "Your current package is not valid for this class because the country does not match."));
        if (userPackage.getRemainingCredit() < classSchedule.getRequiredCredit()) {
            throw new BusinessRuleException("Not enough credits to join the waitlist.");
        }

        userPackage.setRemainingCredit(
                userPackage.getRemainingCredit() - classSchedule.getRequiredCredit());
        userPackageRepository.save(userPackage);

        Waitlist waitlist = new Waitlist();
        waitlist.setUser(user);
        waitlist.setSchedule(classSchedule);
        waitlist.setUserPackage(userPackage);
        waitlist.setJoinedAt(LocalDateTime.now());

        waitlistRepository.save(waitlist);
    }

}
