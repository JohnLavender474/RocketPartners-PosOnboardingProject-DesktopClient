package com.rocketpartners.onboarding.possystem.config;

import com.rocketpartners.onboarding.possystem.display.controller.CustomerViewController;
import com.rocketpartners.onboarding.possystem.display.view.CustomerView;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryTransactionRepository;
import lombok.NonNull;
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

    @Bean
    public CustomerViewController customerViewController(@NonNull CustomerView customerView) {
        return new CustomerViewController(customerView);
    }

    @Bean
    public CustomerView customerView() {
        return new CustomerView();
    }
}
