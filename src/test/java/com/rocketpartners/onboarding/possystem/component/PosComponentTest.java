package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PosComponentTest {

    private PosComponent posComponent;

    @BeforeEach
    void setUp() {
        TransactionService transactionService = mock(TransactionService.class);
        when(transactionService.createAndPersist(anyString(), anyInt())).thenAnswer(invocation -> {
            int transactionNumber = invocation.getArgument(1);
            Transaction transaction = new Transaction();
            transaction.setId("TRANS" + transactionNumber);
            transaction.setPosSystemId(invocation.getArgument(0));
            transaction.setTransactionNumber(transactionNumber);
            return transaction;
        });
        posComponent = Mockito.spy(new PosComponent(transactionService));
        PosSystem posSystem = new PosSystem();
        posSystem.setId("1");
        posSystem.setPosLane(1);
        posSystem.setStoreName("Test Store");
        posComponent.setPosSystem(posSystem);
    }

    @Test
    public void testBootUp() {
        posComponent.bootUp();
        assertTrue(posComponent.isOn());
        assertNull(posComponent.getTransaction());
        assertEquals(TransactionState.NOT_STARTED, posComponent.getTransactionState());

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent).dispatchPosEvent(eventCaptor.capture());
        PosEvent event = eventCaptor.getValue();
        assertEquals(event.getType(), PosEventType.POS_BOOTUP);
        assertTrue(event.containsProperty("posSystemId"));
    }

    @Test
    public void testShutdown() {
        posComponent.bootUp();
        posComponent.shutdown();
        assertNull(posComponent.getTransaction());
        assertEquals(posComponent.getTransactionState(), TransactionState.NOT_STARTED);

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent, times(2)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        assertEquals(2, capturedEvents.size());
        assertEquals(PosEventType.POS_BOOTUP, capturedEvents.get(0).getType());
        assertEquals(PosEventType.POS_SHUTDOWN, capturedEvents.get(1).getType());

        posComponent.update();
        assertFalse(posComponent.isOn());
    }

    @Test
    void testStartTransaction() {
        posComponent.bootUp();
        posComponent.startTransaction();

        assertNotNull(posComponent.getTransaction());
        assertEquals(TransactionState.SCANNING_IN_PROGRESS, posComponent.getTransactionState());

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent, times(2)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        assertEquals(2, capturedEvents.size());
        assertEquals(PosEventType.POS_BOOTUP, capturedEvents.get(0).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(1).getType());
    }


    @Test
    public void testVoidTransaction() {
        posComponent.bootUp();
        posComponent.startTransaction();
        posComponent.voidTransaction();

        assertEquals(TransactionState.VOIDED, posComponent.getTransactionState());

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent, times(3)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        assertEquals(3, capturedEvents.size());
        assertEquals(PosEventType.POS_BOOTUP, capturedEvents.get(0).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(1).getType());
        assertEquals(PosEventType.TRANSACTION_VOIDED, capturedEvents.get(2).getType());
    }

    @Test
    void testCompleteTransaction() {
        posComponent.bootUp();
        posComponent.startTransaction();
        posComponent.completeTransaction();

        assertEquals(TransactionState.COMPLETED, posComponent.getTransactionState());

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent, times(3)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        assertEquals(3, capturedEvents.size());
        assertEquals(PosEventType.POS_BOOTUP, capturedEvents.get(0).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(1).getType());
        assertEquals(PosEventType.TRANSACTION_COMPLETED, capturedEvents.get(2).getType());
    }

    @Test
    public void testResetPos() {
        posComponent.bootUp();
        posComponent.startTransaction();
        posComponent.completeTransaction();
        posComponent.resetPos();

        assertNull(posComponent.getTransaction());
        assertEquals(TransactionState.NOT_STARTED, posComponent.getTransactionState());

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent, times(4)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        assertEquals(4, capturedEvents.size());
        assertEquals(PosEventType.POS_BOOTUP, capturedEvents.get(0).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(1).getType());
        assertEquals(PosEventType.TRANSACTION_COMPLETED, capturedEvents.get(2).getType());
        assertEquals(PosEventType.POS_RESET, capturedEvents.get(3).getType());
    }
}

