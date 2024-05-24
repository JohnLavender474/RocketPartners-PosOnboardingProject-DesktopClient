package com.rocketpartners.onboarding.possystem;

import com.rocketpartners.onboarding.possystem.component.ItemBookLoaderComponent;
import com.rocketpartners.onboarding.possystem.component.PosComponent;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import com.rocketpartners.onboarding.possystem.service.TransactionService;
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
        TransactionService transactionService = Mockito.mock(TransactionService.class);
        ItemService itemService = Mockito.mock(ItemService.class);
        MockPosComponent posComponent = new MockPosComponent(itemBookLoaderComponent, transactionService, itemService);

        Timer timer = new Timer(1000, e -> posComponent.update());
        timer.setRepeats(true);
        timer.start();

        try {
            Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                    assertEquals(3, posComponent.getUpdateCount()));
        } catch (ConditionTimeoutException e) {
            e.printStackTrace();
        } finally {
            timer.stop();
        }
    }

    /**
     * Mock POS component that counts the number of times the update method is called.
     */
    @Getter
    private static class MockPosComponent extends PosComponent {

        private int updateCount;

        /**
         * Constructor that accepts an item book loader component, a transaction service, and an item service.
         *
         * @param itemBookLoaderComponent the item book loader component
         * @param transactionService      the transaction service
         * @param itemService             the item service
         */
        public MockPosComponent(@NonNull ItemBookLoaderComponent itemBookLoaderComponent,
                                @NonNull TransactionService transactionService, @NonNull ItemService itemService) {
            super(itemBookLoaderComponent, transactionService, itemService);
        }

        @Override
        public void update() {
            super.update();
            updateCount++;
        }
    }
}

