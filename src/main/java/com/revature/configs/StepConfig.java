package com.revature.configs;

import com.revature.entities.User;
import com.revature.repositories.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
public class StepConfig {

    @Bean(name = "updateUsersStep")
    public Step updateUsersStep(JpaPagingItemReader<User> userReader, ItemWriter<User> userWriter, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("updateUsersStep", jobRepository)
                .<User, User>chunk(10, transactionManager)
                .reader(userReader)
                .writer(userWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(DataAccessException.class)
                .build();
    }

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

    @Bean
    public ItemWriter<User> userWriter(UserRepository userRepository) {
        return users -> {
            users.getItems().forEach(user -> user.setUsername("User" + user.getId()));
            userRepository.saveAllAndFlush(users.getItems());
        };
    }
}
