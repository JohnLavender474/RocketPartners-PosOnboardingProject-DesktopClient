package com.rocketpartners.onboarding.possystem;

import static org.assertj.core.api.Assertions.assertThat;

import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.h2.H2TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryTransactionRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void contextLoads() {
        assertThat(transactionRepository).isNotNull();
    }

    @Test
    public void testInMemoryTransactionRepository() {
        if (transactionRepository instanceof InMemoryTransactionRepository) {
            logger.info("InMemoryTransactionRepository is loaded");
        } else if (transactionRepository instanceof H2TransactionRepository) {
            logger.info("H2TransactionRepository is loaded");
        }
        assertThat(transactionRepository).isInstanceOfAny(InMemoryTransactionRepository.class, H2TransactionRepository.class);
    }
}

