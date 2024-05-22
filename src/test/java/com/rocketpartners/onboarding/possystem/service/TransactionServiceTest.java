package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.LineItem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository);
    }
    @Test
    public void testAddItemToTransaction_NewItem() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";

        transactionService.addItemToTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertEquals(itemUpc, lineItem.getItemUpc());
        assertEquals(1, lineItem.getQuantity());
        assertFalse(lineItem.isVoided());
    }

    @Test
    public void testAddItemToTransaction_ExistingItem() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false, false);
        transaction.getLineItems().add(existingItem);

        transactionService.addItemToTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertEquals(2, lineItem.getQuantity());
        assertEquals(itemUpc, lineItem.getItemUpc());
        assertFalse(lineItem.isVoided());
    }

    @Test
    public void testAddItemToTransaction_VoidedItem() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem voidedItem = new LineItem(itemUpc, "tx1", 1, true, false);
        transaction.getLineItems().add(voidedItem);

        transactionService.addItemToTransaction(transaction, itemUpc);

        assertEquals(2, transaction.getLineItems().size());
        LineItem newItem = transaction.getLineItems().stream()
                .filter(item -> !item.isVoided())
                .findFirst()
                .orElse(null);

        assertNotNull(newItem);
        assertEquals(itemUpc, newItem.getItemUpc());
        assertEquals(1, newItem.getQuantity());
    }

    @Test
    public void testAddMultipleItemsWithDifferentUPCs() {
        Transaction transaction = new Transaction();
        String itemUpc1 = "1234567890";
        String itemUpc2 = "0987654321";

        transactionService.addItemToTransaction(transaction, itemUpc1);
        transactionService.addItemToTransaction(transaction, itemUpc2);

        assertEquals(2, transaction.getLineItems().size());
        assertTrue(transaction.getLineItems().stream().anyMatch(item -> item.getItemUpc().equals(itemUpc1)));
        assertTrue(transaction.getLineItems().stream().anyMatch(item -> item.getItemUpc().equals(itemUpc2)));
    }

    @Test
    public void testAddItemMultipleTimes() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";

        transactionService.addItemToTransaction(transaction, itemUpc);
        transactionService.addItemToTransaction(transaction, itemUpc);
        transactionService.addItemToTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertEquals(3, lineItem.getQuantity());
    }

    @Test
    public void testRemoveItemFromTransaction_DecrementQuantity() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 2, false, false);
        transaction.getLineItems().add(existingItem);

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertEquals(1, lineItem.getQuantity());
    }

    @Test
    public void testRemoveItemFromTransaction_RemoveLineItem() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false, false);
        transaction.getLineItems().add(existingItem);

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        assertTrue(transaction.getLineItems().isEmpty());
    }

    @Test
    public void testRemoveItemFromTransaction_ItemNotInTransaction() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        assertTrue(transaction.getLineItems().isEmpty());
    }

    @Test
    public void testVoidLineItemInTransaction() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false, false);
        transaction.getLineItems().add(existingItem);

        transactionService.voidLineItemInTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertTrue(lineItem.isVoided());
    }

    @Test
    public void testVoidLineItemInTransaction_AlreadyVoided() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, true, false);
        transaction.getLineItems().add(existingItem);

        transactionService.voidLineItemInTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertTrue(lineItem.isVoided());
        assertEquals(1, lineItem.getQuantity());
    }

    @Test
    public void testAddItemToTransaction_OnlyVoidedItems() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem voidedItem = new LineItem(itemUpc, "tx1", 1, true, false);
        transaction.getLineItems().add(voidedItem);

        transactionService.addItemToTransaction(transaction, itemUpc);

        assertEquals(2, transaction.getLineItems().size());
        LineItem newItem = transaction.getLineItems().stream()
                .filter(item -> !item.isVoided())
                .findFirst()
                .orElse(null);

        assertNotNull(newItem);
        assertEquals(itemUpc, newItem.getItemUpc());
        assertEquals(1, newItem.getQuantity());
    }

    @Test
    public void testRemoveItemFromTransaction_OnlyOneQuantity() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false, false);
        transaction.getLineItems().add(existingItem);

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        assertTrue(transaction.getLineItems().isEmpty());
    }

    @Test
    public void testVoidItemWithMultipleQuantities() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 3, false, false);
        transaction.getLineItems().add(existingItem);

        transactionService.voidLineItemInTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertTrue(lineItem.isVoided());
    }


    @Test
    public void testAddItemToTransaction_DoesNotChangeVoidedQuantity() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem voidedItem = new LineItem(itemUpc, "tx1", 5, true, false);
        transaction.getLineItems().add(voidedItem);

        transactionService.addItemToTransaction(transaction, itemUpc);

        assertEquals(2, transaction.getLineItems().size());
        LineItem newItem = transaction.getLineItems().stream()
                .filter(item -> !item.isVoided())
                .findFirst()
                .orElse(null);

        assertNotNull(newItem);
        assertEquals(itemUpc, newItem.getItemUpc());
        assertEquals(1, newItem.getQuantity());
        assertEquals(5, voidedItem.getQuantity());
    }

    @Test
    public void testRemoveItemFromEmptyTransaction() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        assertTrue(transaction.getLineItems().isEmpty());
    }

    @Test
    public void testVoidItemFromEmptyTransaction() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";

        transactionService.voidLineItemInTransaction(transaction, itemUpc);

        assertTrue(transaction.getLineItems().isEmpty());
    }

    @Test
    public void testTransactionPersistence_AddItem() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";

        transactionService.addItemToTransaction(transaction, itemUpc);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).saveTransaction(transactionCaptor.capture());
        assertEquals(transaction, transactionCaptor.getValue());
    }

    @Test
    public void testTransactionPersistence_RemoveItem() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false, false);
        transaction.getLineItems().add(existingItem);

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).saveTransaction(transactionCaptor.capture());
        assertEquals(transaction, transactionCaptor.getValue());
    }

    @Test
    public void testTransactionPersistence_VoidLineItem() {
        Transaction transaction = new Transaction();
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false, false);
        transaction.getLineItems().add(existingItem);

        transactionService.voidLineItemInTransaction(transaction, itemUpc);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).saveTransaction(transactionCaptor.capture());
        assertEquals(transaction, transactionCaptor.getValue());
    }
}

