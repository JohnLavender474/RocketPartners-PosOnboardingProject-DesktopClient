package com.rocketpartners.onboarding.possystem.component;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@SpringJUnitConfig(BackOfficeComponentTest.Config.class)
public class BackOfficeComponentTest {

    @Mock
    private PosComponent posComponent;

    @SpyBean
    private BackOfficeComponent backOfficeComponent;

    @BeforeEach
    void setUp() {
        backOfficeComponent.addPosComponent(posComponent);
    }

    @Test
    void testBootUp() {
        backOfficeComponent.bootUp();
        verify(posComponent, times(1)).bootUp();
    }

    @Test
    void testShutdown() {
        backOfficeComponent.shutdown();
        verify(posComponent, times(1)).shutdown();
    }

    @Test
    void testScheduledUpdate() {
        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilAsserted(() ->
                verify(posComponent, atLeast(2)).update());
    }

    @Configuration
    @EnableScheduling
    static class Config {

        @Bean
        public BackOfficeComponent backOfficeComponent() {
            return new BackOfficeComponent();
        }

        @Bean
        public PosComponent posComponent() {
            return mock(PosComponent.class);
        }
    }
}