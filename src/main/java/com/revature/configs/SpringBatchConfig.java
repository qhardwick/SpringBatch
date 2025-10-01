package com.revature.configs;

import com.revature.entities.User;
import com.revature.repositories.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
public class SpringBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final UserRepository userRepository;

    @Autowired
    public SpringBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory;
        this.userRepository = userRepository;
    }

    @Bean
    public Job userReportJob(Step updateUsersStep) {
        System.out.println("Starting userReportJob");
        return new JobBuilder("userReportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(updateUsersStep)
                .build();
    }

    @Bean(name = "updateUsersStep")
    public Step updateUsersStep(JpaPagingItemReader<User> userReader, ItemWriter<User> userWriter) {
        System.out.println("Starting updateUserStep");
        return new StepBuilder("updateUsersStep", jobRepository)
                .<User, User>chunk(2, transactionManager)
                .reader(userReader)
                .writer(userWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(DataAccessException.class)
                .listener(new StepExecutionListenerSupport() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        System.out.println("Step starting: " + stepExecution.getStepName());
                    }
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        System.out.println("Step completed: " + stepExecution.getExitStatus() +
                                ", Read count: " + stepExecution.getReadCount() +
                                ", Write count: " + stepExecution.getWriteCount());
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> userReader(@Value("#{jobParameters['year']}") String year) {
        System.out.println("Starting ItemReader");
        System.out.println("For year: " + year);
        if(year == null || year.isBlank()) {
            throw new IllegalArgumentException("Year is required");
        }

        return new JpaPagingItemReaderBuilder<User>()
                .name("userReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT u FROM User u WHERE u.year = :year ORDER BY u.id")
                .parameterValues(Map.of("year", year))
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemWriter<User> userWriter() {
        System.out.println("Updating users");
        return users -> {
            users.getItems().forEach(user -> user.setUsername("User" + user.getId()));
            System.out.println("Saving users: " + users.getItems());
            userRepository.saveAllAndFlush(users.getItems());
        };
    }
}
