package com.rocketpartners.onboarding.possystem.config;

import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryTransactionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "inmemory")
    public TransactionRepository inMemoryTransactionRepository() {
        return new InMemoryTransactionRepository();
    }
}
