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

public class KeypadViewControllerTest {

    private IPosEventDispatcher mockEventDispatcher;
    private KeypadView mockKeypadView;
    private KeypadViewController controller;

    @BeforeEach
    public void setUp() {
        mockEventDispatcher = Mockito.mock(IPosEventDispatcher.class);
        mockKeypadView = Mockito.mock(KeypadView.class);
        controller = new KeypadViewController(mockEventDispatcher, mockKeypadView);
    }

    @Test
    public void testOnPosEventStartPayWithCardProcess() {
        PosEvent event = new PosEvent(PosEventType.START_PAY_WITH_CARD_PROCESS);
        controller.onPosEvent(event);
        verify(mockKeypadView, times(1)).setVisible(true);
    }

    @Test
    public void testOnPosEventDoCancelPayment() {
        PosEvent event = new PosEvent(PosEventType.DO_CANCEL_PAYMENT);
        controller.onPosEvent(event);
        verify(mockKeypadView, times(1)).setVisible(false);
    }

    @Test
    public void testOnPosEventTransactionCompleted() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_COMPLETED);
        controller.onPosEvent(event);
        verify(mockKeypadView, times(1)).setVisible(false);
    }

    @Test
    public void testGetEventTypesToListenFor() {
        Set<PosEventType> eventTypes = controller.getEventTypesToListenFor();
        assert eventTypes.contains(PosEventType.START_PAY_WITH_CARD_PROCESS);
        assert eventTypes.contains(PosEventType.DO_CANCEL_PAYMENT);
        assert eventTypes.contains(PosEventType.TRANSACTION_COMPLETED);
    }

    @Test
    public void testDispatchPosEvent() {
        PosEvent event = new PosEvent(PosEventType.START_PAY_WITH_CARD_PROCESS);
        controller.dispatchPosEvent(event);
        verify(mockEventDispatcher, times(1)).dispatchPosEvent(event);
    }
}

