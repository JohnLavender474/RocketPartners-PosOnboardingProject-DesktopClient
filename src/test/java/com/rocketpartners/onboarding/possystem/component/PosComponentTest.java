package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import com.rocketpartners.onboarding.possystem.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PosComponentTest {

    private TransactionService transactionService;
    private ItemService itemService;
    private PosComponent posComponent;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        when(transactionService.createAndPersist(anyString(), anyInt())).thenAnswer(invocation -> {
            int transactionNumber = invocation.getArgument(1);
            Transaction transaction = new Transaction();
            transaction.setId("TRANS" + transactionNumber);
            transaction.setPosSystemId(invocation.getArgument(0));
            transaction.setTransactionNumber(transactionNumber);
            return transaction;
        });
        itemService = mock(ItemService.class);
        when(itemService.createAndPersist(anyString(), anyString(), any(BigDecimal.class), anyString(), anyString()))
                .thenAnswer(invocation -> {
                    String itemUpc = invocation.getArgument(0);
                    String itemName = invocation.getArgument(1);
                    BigDecimal itemPrice = invocation.getArgument(2);
                    String itemCategory = invocation.getArgument(3);
                    String itemDescription = invocation.getArgument(4);
                    Item item = new Item();
                    item.setUpc(itemUpc);
                    item.setName(itemName);
                    item.setUnitPrice(itemPrice);
                    item.setCategory(itemCategory);
                    item.setDescription(itemDescription);
                    return item;
                });
        posComponent = Mockito.spy(new PosComponent(transactionService, itemService));
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

    @Test
    public void testHandlePosEvent_RequestAddItem_TransactionNotInProgress() {
        PosEvent addItemEvent = new PosEvent(PosEventType.REQUEST_ADD_ITEM, Map.of(ConstKeys.ITEM_UPC, "1234567890"));

        posComponent.dispatchPosEvent(addItemEvent);

        verify(transactionService, never()).addItemToTransaction(any(Transaction.class), anyString());
        verify(itemService, never()).itemExists(anyString());
    }

    @Test
    public void testHandlePosEvent_RequestAddItem_ItemDoesNotExist() {
        posComponent.startTransaction();
        String itemUpc = "1234567890";
        PosEvent addItemEvent = new PosEvent(PosEventType.REQUEST_ADD_ITEM, Map.of(ConstKeys.ITEM_UPC, itemUpc));

        when(itemService.itemExists(itemUpc)).thenReturn(false);

        posComponent.dispatchPosEvent(addItemEvent);

        verify(transactionService, never()).addItemToTransaction(any(Transaction.class), anyString());
        verify(itemService, times(1)).itemExists(itemUpc);
    }

    @Test
    public void testHandlePosEvent_RequestAddItem_TransactionInProgress() {
        posComponent.startTransaction();
        String itemUpc = "1234567890";
        PosEvent addItemEvent = new PosEvent(PosEventType.REQUEST_ADD_ITEM, Map.of(ConstKeys.ITEM_UPC, itemUpc));

        when(itemService.itemExists(itemUpc)).thenReturn(true);

        posComponent.dispatchPosEvent(addItemEvent);

        verify(transactionService, times(1)).addItemToTransaction(any(Transaction.class), eq(itemUpc));
        verify(itemService, times(1)).itemExists(itemUpc);
    }

    @Test
    public void testHandlePosEvent_RequestStartTransaction() {
        PosEvent startTransactionEvent = new PosEvent(PosEventType.REQUEST_START_TRANSACTION);

        posComponent.dispatchPosEvent(startTransactionEvent);

        assertNotNull(posComponent.getTransaction());
        assertEquals(TransactionState.SCANNING_IN_PROGRESS, posComponent.getTransactionState());
    }

    @Test
    public void testHandlePosEvent_RequestVoidTransaction() {
        posComponent.startTransaction();
        PosEvent voidTransactionEvent = new PosEvent(PosEventType.REQUEST_VOID_TRANSACTION);

        posComponent.dispatchPosEvent(voidTransactionEvent);

        assertEquals(TransactionState.VOIDED, posComponent.getTransactionState());
    }

    @Test
    public void testHandlePosEvent_RequestCompleteTransaction1() {
        posComponent.startTransaction();
        posComponent.setTransactionState(TransactionState.AWAITING_CASH_PAYMENT);
        PosEvent completeTransactionEvent = new PosEvent(PosEventType.REQUEST_COMPLETE_TRANSACTION);

        posComponent.dispatchPosEvent(completeTransactionEvent);

        assertEquals(TransactionState.COMPLETED, posComponent.getTransactionState());
    }

    @Test
    public void testHandlePosEvent_RequestCompleteTransaction2() {
        posComponent.startTransaction();
        posComponent.setTransactionState(TransactionState.AWAITING_CARD_PAYMENT);
        PosEvent completeTransactionEvent = new PosEvent(PosEventType.REQUEST_COMPLETE_TRANSACTION);

        posComponent.dispatchPosEvent(completeTransactionEvent);

        assertEquals(TransactionState.COMPLETED, posComponent.getTransactionState());
    }

    @Test
    public void testHandlePosEvent_RequestResetPos() {
        posComponent.startTransaction();
        posComponent.setTransactionState(TransactionState.COMPLETED);
        PosEvent resetPosEvent = new PosEvent(PosEventType.REQUEST_RESET_POS);

        posComponent.dispatchPosEvent(resetPosEvent);

        assertNull(posComponent.getTransaction());
        assertEquals(TransactionState.NOT_STARTED, posComponent.getTransactionState());
    }
}

