package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.constant.ConstVals;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.model.LineItem;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import com.rocketpartners.onboarding.possystem.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PosComponentTest {

    private ItemBookLoaderComponent itemBookLoaderComponent;
    private TransactionService transactionService;
    private ItemService itemService;
    private PosComponent posComponent;

    @BeforeEach
    void setUp() {
        itemBookLoaderComponent = mock(ItemBookLoaderComponent.class);
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
        posComponent = Mockito.spy(new PosComponent(itemBookLoaderComponent, transactionService, itemService));
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

        ArgumentCaptor<ItemService> itemServiceCaptor = ArgumentCaptor.forClass(ItemService.class);
        verify(itemBookLoaderComponent).loadItemBook(itemServiceCaptor.capture());
        assertEquals(itemService, itemServiceCaptor.getValue());

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent).dispatchPosEvent(eventCaptor.capture());
        PosEvent event = eventCaptor.getValue();
        assertEquals(event.getType(), PosEventType.POS_BOOTUP);
        assertTrue(event.containsProperty(ConstKeys.POS_SYSTEM_ID));
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
        posComponent.startTransaction(null);

        assertNotNull(posComponent.getTransaction());
        assertEquals(TransactionState.SCANNING_IN_PROGRESS, posComponent.getTransactionState());

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent, times(3)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        assertEquals(3, capturedEvents.size());
        assertEquals(PosEventType.POS_BOOTUP, capturedEvents.get(0).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(1).getType());
        assertEquals(PosEventType.DO_UPDATE_QUICK_ITEMS, capturedEvents.get(2).getType());
    }


    @Test
    public void testVoidTransaction() {
        posComponent.bootUp();
        posComponent.startTransaction(null);
        posComponent.voidTransaction();

        assertEquals(TransactionState.VOIDED, posComponent.getTransactionState());

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent, times(4)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        assertEquals(4, capturedEvents.size());
        assertEquals(PosEventType.POS_BOOTUP, capturedEvents.get(0).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(1).getType());
        assertEquals(PosEventType.DO_UPDATE_QUICK_ITEMS, capturedEvents.get(2).getType());
        assertEquals(PosEventType.TRANSACTION_VOIDED, capturedEvents.get(3).getType());
    }

    @Test
    void testCompleteTransaction() {
        posComponent.bootUp();
        posComponent.startTransaction(null);
        posComponent.completeTransaction();

        assertEquals(TransactionState.COMPLETED, posComponent.getTransactionState());

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent, times(4)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        assertEquals(4, capturedEvents.size());
        assertEquals(PosEventType.POS_BOOTUP, capturedEvents.get(0).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(1).getType());
        assertEquals(PosEventType.DO_UPDATE_QUICK_ITEMS, capturedEvents.get(2).getType());
        assertEquals(PosEventType.TRANSACTION_COMPLETED, capturedEvents.get(3).getType());
    }

    @Test
    public void testResetPos() {
        posComponent.bootUp();
        posComponent.startTransaction(null);
        posComponent.completeTransaction();
        posComponent.resetPos();

        assertNull(posComponent.getTransaction());
        assertEquals(TransactionState.NOT_STARTED, posComponent.getTransactionState());

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        verify(posComponent, times(5)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        assertEquals(5, capturedEvents.size());
        assertEquals(PosEventType.POS_BOOTUP, capturedEvents.get(0).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(1).getType());
        assertEquals(PosEventType.DO_UPDATE_QUICK_ITEMS, capturedEvents.get(2).getType());
        assertEquals(PosEventType.TRANSACTION_COMPLETED, capturedEvents.get(3).getType());
        assertEquals(PosEventType.POS_RESET, capturedEvents.get(4).getType());
    }

    @Test
    public void testHandlePosEvent_RequestAddItem_TransactionNotStarted_ItemExists() {
        posComponent.setTransactionState(TransactionState.NOT_STARTED);
        String itemUpc = "1234567890";
        PosEvent addItemEvent = new PosEvent(PosEventType.REQUEST_ADD_ITEM, Map.of(ConstKeys.ITEM_UPC, itemUpc));

        when(itemService.itemExists(itemUpc)).thenReturn(true);
        when(transactionService.addItemToTransaction(any(), any())).thenReturn(true);

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        posComponent.dispatchPosEvent(addItemEvent);
        verify(posComponent, times(6)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        System.out.println(capturedEvents);

        assertEquals(6, capturedEvents.size());
        assertEquals(PosEventType.REQUEST_ADD_ITEM, capturedEvents.get(0).getType());
        assertEquals(PosEventType.REQUEST_START_TRANSACTION, capturedEvents.get(1).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(2).getType());
        assertEquals(PosEventType.DO_UPDATE_QUICK_ITEMS, capturedEvents.get(3).getType());
        assertEquals(PosEventType.REQUEST_ADD_ITEM, capturedEvents.get(4).getType());
        assertEquals(PosEventType.ITEM_ADDED, capturedEvents.get(5).getType());
        verify(itemService).itemExists(anyString());
        verify(transactionService, times(1)).addItemToTransaction(any(Transaction.class), anyString());
    }

    @Test
    public void testHandlePosEvent_RequestAddItem_TransactionNotStarted_ItemDoesNotExist() {
        posComponent.setTransactionState(TransactionState.NOT_STARTED);
        String itemUpc = "1234567890";
        PosEvent addItemEvent = new PosEvent(PosEventType.REQUEST_ADD_ITEM, Map.of(ConstKeys.ITEM_UPC, itemUpc));

        when(itemService.itemExists(itemUpc)).thenReturn(false);

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        posComponent.dispatchPosEvent(addItemEvent);
        verify(posComponent, times(6)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        System.out.println(capturedEvents);

        assertEquals(6, capturedEvents.size());
        assertEquals(PosEventType.REQUEST_ADD_ITEM, capturedEvents.get(0).getType());
        assertEquals(PosEventType.REQUEST_START_TRANSACTION, capturedEvents.get(1).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(2).getType());
        assertEquals(PosEventType.DO_UPDATE_QUICK_ITEMS, capturedEvents.get(3).getType());
        assertEquals(PosEventType.REQUEST_ADD_ITEM, capturedEvents.get(4).getType());
        assertEquals(PosEventType.ERROR, capturedEvents.get(5).getType());
        verify(itemService).itemExists(anyString());
        verify(transactionService, never()).addItemToTransaction(any(Transaction.class), anyString());
    }

    @Test
    public void testHandlePosEvent_RequestAddItem_TransactionNotStarted_AddToTransactionFails() {
        posComponent.setTransactionState(TransactionState.NOT_STARTED);
        String itemUpc = "1234567890";
        PosEvent addItemEvent = new PosEvent(PosEventType.REQUEST_ADD_ITEM, Map.of(ConstKeys.ITEM_UPC, itemUpc));

        when(itemService.itemExists(itemUpc)).thenReturn(true);
        when(transactionService.addItemToTransaction(any(), any())).thenReturn(false);

        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        posComponent.dispatchPosEvent(addItemEvent);
        verify(posComponent, times(5)).dispatchPosEvent(eventCaptor.capture());
        List<PosEvent> capturedEvents = eventCaptor.getAllValues();
        System.out.println(capturedEvents);

        assertEquals(5, capturedEvents.size());
        assertEquals(PosEventType.REQUEST_ADD_ITEM, capturedEvents.get(0).getType());
        assertEquals(PosEventType.REQUEST_START_TRANSACTION, capturedEvents.get(1).getType());
        assertEquals(PosEventType.TRANSACTION_STARTED, capturedEvents.get(2).getType());
        assertEquals(PosEventType.DO_UPDATE_QUICK_ITEMS, capturedEvents.get(3).getType());
        assertEquals(PosEventType.REQUEST_ADD_ITEM, capturedEvents.get(4).getType());
        verify(itemService).itemExists(anyString());
        verify(transactionService, times(1)).addItemToTransaction(any(Transaction.class), anyString());
    }

    @Test
    public void testHandlePosEvent_RequestAddItem_ItemDoesNotExist() {
        posComponent.startTransaction(null);
        String itemUpc = "1234567890";
        PosEvent addItemEvent = new PosEvent(PosEventType.REQUEST_ADD_ITEM, Map.of(ConstKeys.ITEM_UPC, itemUpc));

        when(itemService.itemExists(itemUpc)).thenReturn(false);

        posComponent.dispatchPosEvent(addItemEvent);

        verify(transactionService, never()).addItemToTransaction(any(Transaction.class), anyString());
        verify(itemService, times(1)).itemExists(itemUpc);
    }

    @Test
    public void testHandlePosEvent_RequestAddItem_TransactionInProgress() {
        posComponent.startTransaction(null);
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
        posComponent.startTransaction(null);
        PosEvent voidTransactionEvent = new PosEvent(PosEventType.REQUEST_VOID_TRANSACTION);

        posComponent.dispatchPosEvent(voidTransactionEvent);

        assertEquals(TransactionState.VOIDED, posComponent.getTransactionState());
    }

    @Test
    public void testHandlePosEvent_RequestCompleteTransaction1() {
        posComponent.startTransaction(null);
        posComponent.setTransactionState(TransactionState.AWAITING_CASH_PAYMENT);
        PosEvent completeTransactionEvent = new PosEvent(PosEventType.REQUEST_COMPLETE_TRANSACTION);

        posComponent.dispatchPosEvent(completeTransactionEvent);

        assertEquals(TransactionState.COMPLETED, posComponent.getTransactionState());
    }

    @Test
    public void testHandlePosEvent_RequestCompleteTransaction2() {
        posComponent.startTransaction(null);
        posComponent.setTransactionState(TransactionState.AWAITING_CARD_PAYMENT);
        PosEvent completeTransactionEvent = new PosEvent(PosEventType.REQUEST_COMPLETE_TRANSACTION);

        posComponent.dispatchPosEvent(completeTransactionEvent);

        assertEquals(TransactionState.COMPLETED, posComponent.getTransactionState());
    }

    @Test
    public void testHandlePosEvent_RequestResetPos() {
        posComponent.startTransaction(null);
        posComponent.setTransactionState(TransactionState.COMPLETED);
        PosEvent resetPosEvent = new PosEvent(PosEventType.REQUEST_RESET_POS);

        posComponent.dispatchPosEvent(resetPosEvent);

        assertNull(posComponent.getTransaction());
        assertEquals(TransactionState.NOT_STARTED, posComponent.getTransactionState());
    }

    @Test
    public void testHandlePosEvent_RequestRemoveItem_TransactionNotInProgress() {
        PosEvent removeItemEvent = new PosEvent(PosEventType.REQUEST_REMOVE_ITEM, Map.of(ConstKeys.ITEM_UPC,
                "1234567890"));

        posComponent.dispatchPosEvent(removeItemEvent);

        verify(transactionService, never()).removeItemFromTransaction(any(Transaction.class), anyString());
        verify(itemService, never()).itemExists(anyString());
    }

    @Test
    public void testHandlePosEvent_RequestRemoveItem_ItemDoesNotExist() {
        posComponent.startTransaction(null);
        String itemUpc = "1234567890";
        PosEvent removeItemEvent = new PosEvent(PosEventType.REQUEST_REMOVE_ITEM, Map.of(ConstKeys.ITEM_UPC, itemUpc));

        when(itemService.itemExists(itemUpc)).thenReturn(false);

        posComponent.dispatchPosEvent(removeItemEvent);

        verify(transactionService, never()).removeItemFromTransaction(any(Transaction.class), anyString());
        verify(itemService, times(1)).itemExists(itemUpc);
    }

    @Test
    public void testHandlePosEvent_RequestRemoveItem_TransactionInProgress() {
        posComponent.startTransaction(null);
        String itemUpc = "1234567890";
        PosEvent removeItemEvent = new PosEvent(PosEventType.REQUEST_REMOVE_ITEM, Map.of(ConstKeys.ITEM_UPC, itemUpc));

        when(itemService.itemExists(itemUpc)).thenReturn(true);
        when(transactionService.removeItemFromTransaction(any(Transaction.class), eq(itemUpc))).thenReturn(true);

        posComponent.dispatchPosEvent(removeItemEvent);

        verify(transactionService, times(1)).removeItemFromTransaction(any(Transaction.class), eq(itemUpc));
        verify(itemService, times(1)).itemExists(itemUpc);
    }

    @Test
    public void testHandlePosEvent_RequestVoidLineItems_TransactionNotStarted() {
        PosEvent voidLineItemsEvent = new PosEvent(PosEventType.REQUEST_VOID_LINE_ITEMS, Map.of(ConstKeys.ITEM_UPCS,
                List.of("1234567890")));

        posComponent.dispatchPosEvent(voidLineItemsEvent);

        verify(transactionService, never()).voidLineItemInTransaction(any(Transaction.class), anyString());
    }

    @Test
    public void testHandlePosEvent_RequestVoidLineItems_TransactionInProgress() {
        posComponent.startTransaction(null);
        PosEvent voidLineItemsEvent = new PosEvent(PosEventType.REQUEST_VOID_LINE_ITEMS, Map.of(ConstKeys.ITEM_UPCS,
                List.of("1234567890")));

        when(itemService.itemExists("1234567890")).thenReturn(true);

        posComponent.dispatchPosEvent(voidLineItemsEvent);

        verify(transactionService, times(1)).voidLineItemInTransaction(any(Transaction.class), eq("1234567890"));
    }

    @Test
    public void testHandlePosEvent_RequestUpdateQuickItems_TransactionNotInProgress() {
        PosEvent updateQuickItemsEvent = new PosEvent(PosEventType.REQUEST_UPDATE_QUICK_ITEMS);

        posComponent.dispatchPosEvent(updateQuickItemsEvent);

        verify(itemService, never()).getRandomItemsNotIn(anySet(), anyInt());
    }

    @Test
    public void testHandlePosEvent_RequestUpdateQuickItems_TransactionInProgress() {
        posComponent.startTransaction(null);
        PosEvent updateQuickItemsEvent = new PosEvent(PosEventType.REQUEST_UPDATE_QUICK_ITEMS);

        posComponent.dispatchPosEvent(updateQuickItemsEvent);

        verify(itemService, times(1)).getRandomItemsNotIn(anySet(), eq(ConstVals.QUICK_ITEMS_COUNT));
    }

    @Test
    void testStartCardPaymentProcess() {
        Transaction transaction = new Transaction();
        LineItem lineItem = new LineItem();
        lineItem.setItemUpc("testUPC");
        lineItem.setQuantity(1);
        transaction.setLineItems(Collections.singletonList(lineItem));
        when(transactionService.createAndPersist(anyString(), anyInt())).thenReturn(transaction);

        posComponent.startTransaction(null);
        posComponent.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_START_PAY_WITH_CARD_PROCESS));

        assertEquals(TransactionState.AWAITING_CARD_PAYMENT, posComponent.getTransactionState());
    }

    @Test
    void testCancelCardPaymentProcess() {
        Transaction transaction = new Transaction();
        LineItem lineItem = new LineItem();
        lineItem.setItemUpc("testUPC");
        lineItem.setQuantity(1);
        transaction.setLineItems(Collections.singletonList(lineItem));

        when(transactionService.createAndPersist(anyString(), anyInt())).thenReturn(transaction);

        posComponent.startTransaction(null);
        posComponent.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_START_PAY_WITH_CARD_PROCESS));
        posComponent.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_CANCEL_PAYMENT));

        assertEquals(TransactionState.SCANNING_IN_PROGRESS, posComponent.getTransactionState());
    }

    @Test
    void testCompleteCardPaymentProcess() {
        Transaction transaction = new Transaction();
        LineItem lineItem = new LineItem();
        lineItem.setItemUpc("testUPC");
        lineItem.setQuantity(1);
        transaction.setLineItems(Collections.singletonList(lineItem));
        when(transactionService.createAndPersist(anyString(), anyInt())).thenReturn(transaction);

        posComponent.startTransaction(null);
        posComponent.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_START_PAY_WITH_CARD_PROCESS));
        posComponent.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_COMPLETE_TRANSACTION));

        assertEquals(TransactionState.COMPLETED, posComponent.getTransactionState());
        assertTrue(posComponent.isTransactionTendered());
        verify(transactionService, times(1)).saveTransaction(transaction);
    }
}

