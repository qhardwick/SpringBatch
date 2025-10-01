package com.revature.controllers;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job userReportJob;

    @Autowired
    public JobController(JobLauncher jobLauncher, Job userReportJob) {
        this.jobLauncher = jobLauncher;
        this.userReportJob = userReportJob;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello Job Controller";
    }

    @GetMapping("/{year}")
    public JobExecution runUserReportJobForYear(@PathVariable("year") String year) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("year", year)  // Wraps as JobParameter<String>
                .addLong("run.id", System.currentTimeMillis())  // Ensures unique job instance
                .toJobParameters();
        return jobLauncher.run(userReportJob, jobParameters);
    }
}
