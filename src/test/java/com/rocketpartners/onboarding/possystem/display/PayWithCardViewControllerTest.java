package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PayWithCardViewControllerTest {

    private IPosEventDispatcher mockEventDispatcher;
    private PayWithCardView mockPayWithCardView;
    private PayWithCardViewController controller;

    @BeforeEach
    void setUp() {
        mockEventDispatcher = Mockito.mock(IPosEventDispatcher.class);
        mockPayWithCardView = Mockito.mock(PayWithCardView.class);
        controller = new PayWithCardViewController(mockEventDispatcher, mockPayWithCardView);
    }

    @Test
    void testOnPosEventStartPayWithCardProcess() {
        PosEvent event = new PosEvent(PosEventType.START_PAY_WITH_CARD_PROCESS);
        controller.onPosEvent(event);
        verify(mockPayWithCardView, times(1)).setVisible(true);
    }

    @Test
    void testOnPosEventDoCancelPayment() {
        PosEvent event = new PosEvent(PosEventType.DO_CANCEL_PAYMENT);
        controller.onPosEvent(event);
        verify(mockPayWithCardView, times(1)).setVisible(false);
    }

    @Test
    void testOnPosEventTransactionCompleted() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_COMPLETED);
        controller.onPosEvent(event);
        verify(mockPayWithCardView, times(1)).setVisible(false);
    }

    @Test
    void testGetEventTypesToListenFor() {
        Set<PosEventType> eventTypes = controller.getEventTypesToListenFor();
        assert eventTypes.contains(PosEventType.START_PAY_WITH_CARD_PROCESS);
        assert eventTypes.contains(PosEventType.DO_CANCEL_PAYMENT);
        assert eventTypes.contains(PosEventType.TRANSACTION_COMPLETED);
    }

    @Test
    void testDispatchPosEvent() {
        PosEvent event = new PosEvent(PosEventType.START_PAY_WITH_CARD_PROCESS);
        controller.dispatchPosEvent(event);
        verify(mockEventDispatcher, times(1)).dispatchPosEvent(event);
    }
}

