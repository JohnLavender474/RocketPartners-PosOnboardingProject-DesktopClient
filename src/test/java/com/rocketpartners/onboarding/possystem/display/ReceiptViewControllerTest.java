package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.display.dto.TransactionDto;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.mockito.Mockito.*;

class ReceiptViewControllerTest {

    private ReceiptView mockReceiptView;
    private ReceiptViewController controller;

    @BeforeEach
    void setUp() {
        mockReceiptView = Mockito.mock(ReceiptView.class);
        controller = new ReceiptViewController(mockReceiptView);
    }

    @Test
    void testOnPosEventTransactionCompleted() {
        PosEvent event = Mockito.mock(PosEvent.class);
        TransactionDto mockTransactionDto = Mockito.mock(TransactionDto.class);

        when(event.getType()).thenReturn(PosEventType.TRANSACTION_COMPLETED);
        when(event.getProperty(ConstKeys.TRANSACTION_DTO, TransactionDto.class)).thenReturn(mockTransactionDto);

        controller.onPosEvent(event);

        verify(mockReceiptView, times(1)).update(mockTransactionDto);
        verify(mockReceiptView, times(1)).setVisible(true);
    }

    @Test
    void testOnPosEventTransactionStarted() {
        PosEvent event = Mockito.mock(PosEvent.class);

        when(event.getType()).thenReturn(PosEventType.TRANSACTION_STARTED);

        controller.onPosEvent(event);

        verify(mockReceiptView, times(1)).setVisible(false);
    }

    @Test
    void testOnPosEventTransactionVoided() {
        PosEvent event = Mockito.mock(PosEvent.class);

        when(event.getType()).thenReturn(PosEventType.TRANSACTION_VOIDED);

        controller.onPosEvent(event);

        verify(mockReceiptView, times(1)).setVisible(false);
    }

    @Test
    void testOnPosEventPosReset() {
        PosEvent event = Mockito.mock(PosEvent.class);

        when(event.getType()).thenReturn(PosEventType.POS_RESET);

        controller.onPosEvent(event);

        verify(mockReceiptView, times(1)).setVisible(false);
    }

    @Test
    void testOnPosEventPosBootup() {
        PosEvent event = Mockito.mock(PosEvent.class);

        when(event.getType()).thenReturn(PosEventType.POS_BOOTUP);

        controller.onPosEvent(event);

        verify(mockReceiptView, times(1)).setVisible(false);
    }

    @Test
    void testGetEventTypesToListenFor() {
        Set<PosEventType> eventTypes = controller.getEventTypesToListenFor();
        assert eventTypes.contains(PosEventType.TRANSACTION_COMPLETED);
        assert eventTypes.contains(PosEventType.TRANSACTION_STARTED);
        assert eventTypes.contains(PosEventType.TRANSACTION_VOIDED);
        assert eventTypes.contains(PosEventType.POS_RESET);
        assert eventTypes.contains(PosEventType.POS_BOOTUP);
    }
}
