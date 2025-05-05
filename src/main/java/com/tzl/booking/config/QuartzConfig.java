package com.tzl.booking.config;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tzl.booking.job.RefundJob;

@Configuration
public class QuartzConfig {

    @Value("${refund.job.cron}")
    private String refundJobCron;

    @Bean
    public JobDetail refundJobDetail() {
        return JobBuilder.newJob(RefundJob.class)
                .withIdentity("refundJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger refundJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(refundJobDetail())
                .withIdentity("refundTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(refundJobCron))
                .build();
    }
}
