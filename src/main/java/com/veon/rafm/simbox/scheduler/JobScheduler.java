package com.veon.rafm.simbox.scheduler;

import org.quartz.*;

import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class JobScheduler {

    public static void main(String[] args) throws Exception {

        String jarPath="d";
        String encryptedPath="c";
        String password="b";
        String mainClass="a";
        String cronString ="0 0/1 * 1/1 * ? *";
        JobBuilder jobBuilder = JobBuilder.newJob(SparkJob.class);
        JobDetail job = jobBuilder.withIdentity("myJob", "group1").build();
        Date sheduleTime = new Date(new Date().getTime() + 5000);
       // Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").startAt(sheduleTime).build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("dummyTriggerName", "group1")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule(cronString))
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        //Below line sets a variable named myContextVar in SchedulerContext.
        //Not only strings, you can set any type of object here.
        scheduler.getContext().put("jarPath",jarPath);
        scheduler.getContext().put("encryptedPath",encryptedPath);
        scheduler.getContext().put("password", password);
        scheduler.getContext().put("mainClass",mainClass);
        scheduler.start();
        scheduler.scheduleJob(job, trigger);

        while(true);
    }


}
