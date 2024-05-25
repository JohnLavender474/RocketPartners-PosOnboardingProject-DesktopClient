package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.LineItem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private DiscountService discountService;
    private ItemService itemService;
    private TaxService taxService;
    private TransactionService transactionService;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        discountService = Mockito.mock(DiscountService.class);
        when(discountService.computeDiscountsToApplyFor(any())).thenReturn(new ArrayList<>());
        when(discountService.computeDiscountAmountFor(any())).thenReturn(BigDecimal.ZERO);
        itemService = Mockito.mock(ItemService.class);
        taxService = Mockito.mock(TaxService.class);
        when(taxService.computeTaxesFor(any())).thenReturn(BigDecimal.valueOf(0.04));
        transactionService = new TransactionService(transactionRepository, discountService, itemService, taxService);
        transaction = new Transaction();
        transaction.setId("tx1");
        transaction.setPosSystemId("pos1");
        transaction.setTransactionNumber(1);
    }

    @Test
    public void testAddItemToTransaction_NewItem() {
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
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false);
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
        transaction.setId("tx1");
        String itemUpc = "1234567890";
        LineItem voidedItem = new LineItem(itemUpc, "tx1", 1, true);
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
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 2, false);
        transaction.getLineItems().add(existingItem);

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertEquals(1, lineItem.getQuantity());
    }

    @Test
    public void testRemoveItemFromTransaction_RemoveLineItem() {
        transaction.setId("tx1");
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 2, false);
        transaction.getLineItems().add(existingItem);

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().get(0).getQuantity());
    }

    @Test
    public void testRemoveItemFromTransaction_ItemNotInTransaction() {
        String itemUpc = "1234567890";

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        assertTrue(transaction.getLineItems().isEmpty());
    }

    @Test
    public void testVoidLineItemInTransaction() {
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false);
        transaction.getLineItems().add(existingItem);

        transactionService.voidLineItemInTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertTrue(lineItem.isVoided());
    }

    @Test
    public void testVoidLineItemInTransaction_AlreadyVoided() {
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, true);
        transaction.getLineItems().add(existingItem);

        transactionService.voidLineItemInTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertTrue(lineItem.isVoided());
        assertEquals(1, lineItem.getQuantity());
    }

    @Test
    public void testAddItemToTransaction_OnlyVoidedItems() {
        transaction.setId("tx1");
        String itemUpc = "1234567890";
        LineItem voidedItem = new LineItem(itemUpc, "tx1", 1, true);
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
    public void testRemoveItemFromTransaction_OnlyOneQuantity1() {
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 2, false);
        transaction.getLineItems().add(existingItem);

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().get(0).getQuantity());
    }

    @Test
    public void testRemoveItemFromTransaction_OnlyOneQuantity2() {
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false);
        transaction.getLineItems().add(existingItem);

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().get(0).getQuantity());
    }

    @Test
    public void testVoidItemWithMultipleQuantities() {
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 3, false);
        transaction.getLineItems().add(existingItem);

        transactionService.voidLineItemInTransaction(transaction, itemUpc);

        assertEquals(1, transaction.getLineItems().size());
        LineItem lineItem = transaction.getLineItems().get(0);
        assertTrue(lineItem.isVoided());
    }


    @Test
    public void testAddItemToTransaction_DoesNotChangeVoidedQuantity() {
        String itemUpc = "1234567890";
        LineItem voidedItem = new LineItem(itemUpc, "tx1", 5, true);
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
        String itemUpc = "1234567890";
        transactionService.removeItemFromTransaction(transaction, itemUpc);
        assertTrue(transaction.getLineItems().isEmpty());
    }

    @Test
    public void testVoidItemFromEmptyTransaction() {
        String itemUpc = "1234567890";
        transactionService.voidLineItemInTransaction(transaction, itemUpc);
        assertTrue(transaction.getLineItems().isEmpty());
    }

    @Test
    public void testTransactionPersistence_AddItem() {
        String itemUpc = "1234567890";
        transactionService.addItemToTransaction(transaction, itemUpc);
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).saveTransaction(transactionCaptor.capture());
        assertEquals(transaction, transactionCaptor.getValue());
    }

    @Test
    public void testTransactionPersistence_RemoveItem() {
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false);
        transaction.getLineItems().add(existingItem);

        transactionService.removeItemFromTransaction(transaction, itemUpc);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).saveTransaction(transactionCaptor.capture());
        assertEquals(transaction, transactionCaptor.getValue());
    }

    @Test
    public void testTransactionPersistence_VoidLineItem() {
        String itemUpc = "1234567890";
        LineItem existingItem = new LineItem(itemUpc, "tx1", 1, false);
        transaction.getLineItems().add(existingItem);

        transactionService.voidLineItemInTransaction(transaction, itemUpc);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).saveTransaction(transactionCaptor.capture());
        assertEquals(transaction, transactionCaptor.getValue());
    }
}

