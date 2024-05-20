package com.rocketpartners.onboarding.possystem.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    /*
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
     */

    // TODO: uncomment the following beans when more than just the in-memory repositories are implemented
    /*
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
     */
}
