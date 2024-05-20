package com.rocketpartners.onboarding.possystem.config;

import com.rocketpartners.onboarding.possystem.component.BackOfficeComponent;
import com.rocketpartners.onboarding.possystem.component.CloudApiComponent;
import com.rocketpartners.onboarding.possystem.component.PosComponent;
import com.rocketpartners.onboarding.possystem.factory.TransactionFactory;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryPosSystemRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryTransactionRepository;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public BackOfficeComponent backOfficeComponent() {
        return new BackOfficeComponent();
    }

    @Bean
    public CloudApiComponent cloudApiComponent() {
        return new CloudApiComponent();
    }

    @Bean
    public PosComponent posComponent(@NonNull TransactionFactory transactionFactory) {
        return new PosComponent(transactionFactory);
    }

    @Bean
    public TransactionFactory transactionFactory(@NonNull TransactionRepository transactionRepository) {
        return new TransactionFactory(transactionRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "inmemory")
    public TransactionRepository inMemoryTransactionRepository() {
        return new InMemoryTransactionRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "inmemory")
    public PosSystemRepository inMemoryPosSystemRepository() {
        return new InMemoryPosSystemRepository();
    }
}
