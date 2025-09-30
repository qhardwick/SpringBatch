package com.revature;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class SpringBatch {

    private final JobLauncher jobLauncher;
    private final Job userReportJob;

    @Autowired
    public SpringBatch(JobLauncher jobLauncher, Job userReportJob) {
        this.jobLauncher = jobLauncher;
        this.userReportJob = userReportJob;
    }

    public static void main(String[] args) {
        System.out.println("Args: " + Arrays.toString(args));
        SpringApplication.run(SpringBatch.class, args);
    }
}
