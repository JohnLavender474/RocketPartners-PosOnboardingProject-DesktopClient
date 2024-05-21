package com.rocketpartners.onboarding.possystem.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BackOfficeComponentTest {

    @Mock
    private PosComponent posComponent;

    @Spy
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
        backOfficeComponent.update();
        backOfficeComponent.update();
        verify(posComponent, times(2)).update();
    }
}