
package com.tzl.booking.job;

import com.tzl.booking.entity.ClassSchedule;
import com.tzl.booking.entity.UserPackage;
import com.tzl.booking.entity.Waitlist;
import com.tzl.booking.repository.UserPackageRepository;
import com.tzl.booking.repository.WaitlistRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RefundJob implements Job {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserPackageRepository userPackageRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LocalDateTime now = LocalDateTime.now();

        List<Waitlist> waitlists = waitlistRepository.findByRefundedFalse();
        for (Waitlist waitlist : waitlists) {
            ClassSchedule schedule = waitlist.getSchedule();
            if (schedule.getEndTime().isBefore(now)) {
                UserPackage userPackage = waitlist.getUserPackage();
                userPackage.setRemainingCredit(userPackage.getRemainingCredit() + schedule.getRequiredCredit());
                userPackageRepository.save(userPackage);
                waitlist.setRefunded(true);
                waitlistRepository.save(waitlist);
            }
        }

        System.out.println("RefundJob executed at " + now);
    }
}
