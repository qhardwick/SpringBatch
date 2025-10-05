package com.revature.configs;

import com.revature.entities.User;
import com.revature.repositories.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
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
public class StepConfig {

    // Provided by SpringBatch:
    JobRepository jobRepository;
    PlatformTransactionManager transactionManager;

    @Autowired
    public StepConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean(name = "updateUsersStep")
    public Step updateUsersStep(JpaPagingItemReader<User> userReader, ItemProcessor<User, User> userProcessor, ItemWriter<User> userWriter) {
        return new StepBuilder("updateUsersStep", jobRepository)
                .<User, User>chunk(10, transactionManager)
                .reader(userReader)
                .processor(userProcessor)
                .writer(userWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(DataAccessException.class)
                .build();
    }

    // The reader is where we fetch the data we wish to process:
    @Bean
    @StepScope
    public JpaPagingItemReader<User> userReader(@Value("#{jobParameters['year']}") String year, EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<User>()
                .name("userReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT u FROM User u WHERE u.year = :year ORDER BY u.id")
                .parameterValues(Map.of("year", year))
                .pageSize(10)
                .build();
    }

    // The processor is where we transform or enrich each element in our data collection. Note: if you need to send the entire collection to an
    // external api, you might want to do that in the writer to avoid making a separate call for each entity. Here, we are taking each User object
    // and updating it, so it comes in as a User and exits as a User, hence ItemProcessor<User, User>:
    @Bean
    public ItemProcessor<User, User> userProcessor() {
        return user -> {
            user.setUsername("User" + user.getId());
            return user;
        };
    }

    // The writer is where we persist our changes for each chunk. Note that chunk does not necessarily equal page size. Page is how many rows you pull out at a time,
    // chunk is how many you process for each commit:
    @Bean
    public ItemWriter<User> userWriter(UserRepository userRepository) {
        return userRepository::saveAll;
    }
}
