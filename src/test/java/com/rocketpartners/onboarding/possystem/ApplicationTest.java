package com.rocketpartners.onboarding.possystem;

import com.rocketpartners.onboarding.possystem.component.BackOfficeComponent;
import lombok.Getter;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {

    @Test
    public void testTimerUpdatesBackOfficeComponent() {
        MockBackOfficeComponent mockBackOfficeComponent = new MockBackOfficeComponent();

        Timer timer = new Timer(1000, e -> mockBackOfficeComponent.update());
        timer.setRepeats(true);
        timer.start();

        try {
            Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                    assertEquals(3, mockBackOfficeComponent.getUpdateCount()));
        } catch (ConditionTimeoutException e) {
            e.printStackTrace();
        } finally {
            timer.stop();
        }
    }

    @Getter
    private static class MockBackOfficeComponent extends BackOfficeComponent {

        private int updateCount = 0;

        @Override
        public void update() {
            updateCount++;
        }
    }
}

