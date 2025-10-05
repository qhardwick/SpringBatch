package com.revature.controllers;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job userReportJob;

    @Autowired
    public JobController(@Qualifier("asyncJobLauncher") JobLauncher jobLauncher, Job userReportJob) {
        this.jobLauncher = jobLauncher;
        this.userReportJob = userReportJob;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello Job Controller";
    }

    @GetMapping(path = "/user-report-job", params = "year")
    public String runUserReportJobForYear(@RequestParam("year") String year) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("year", year)
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(userReportJob, jobParameters);

        return "Starting User Report job for: " + year;
    }
}
