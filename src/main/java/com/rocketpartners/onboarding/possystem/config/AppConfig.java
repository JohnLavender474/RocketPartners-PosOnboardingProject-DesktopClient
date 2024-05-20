package com.rocketpartners.onboarding.possystem.config;

import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.h2.H2TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryTransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
public class AppConfig {

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "inmemory")
    public TransactionRepository inMemoryTransactionRepository() {
        return new InMemoryTransactionRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "h2")
    public TransactionRepository h2TransactionRepository() {
        return new H2TransactionRepository();
    }
}