package com.rocketpartners.onboarding.possystem;

import com.rocketpartners.onboarding.possystem.component.BackOfficeComponent;
import com.rocketpartners.onboarding.possystem.component.ItemBookLoaderComponent;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import lombok.Getter;
import lombok.NonNull;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {

    @Test
    public void testTimerUpdatesBackOfficeComponent() {
        ItemBookLoaderComponent itemBookLoaderComponent = Mockito.mock(ItemBookLoaderComponent.class);
        ItemService itemService = Mockito.mock(ItemService.class);
        MockBackOfficeComponent mockBackOfficeComponent = new MockBackOfficeComponent(itemBookLoaderComponent,
                itemService);

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

    /**
     * Mock back office component that counts the number of updates.
     */
    @Getter
    private static class MockBackOfficeComponent extends BackOfficeComponent {

        private int updateCount = 0;

        /**
         * Constructor that initializes the list of POS components.
         *
         * @param itemBookLoaderComponent the item book loader component
         * @param itemService             the item service
         */
        public MockBackOfficeComponent(@NonNull ItemBookLoaderComponent itemBookLoaderComponent,
                                       @NonNull ItemService itemService) {
            super(itemBookLoaderComponent, itemService);
        }

        @Override
        public void update() {
            updateCount++;
        }
    }
}

