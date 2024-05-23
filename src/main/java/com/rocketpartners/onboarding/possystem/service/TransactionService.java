package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.model.LineItem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service class for Transaction objects.
 */
@SuppressWarnings("DuplicatedCode")
@ToString
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Create a new Transaction object and persist it.
     *
     * @param posSystemId       the ID of the POS system
     * @param transactionNumber the transaction number
     * @return the created and persisted Transaction object
     */
    public Transaction createAndPersist(String posSystemId, int transactionNumber) {
        if (Application.DEBUG) {
            System.out.println("[TransactionService] Creating transaction for POS system ID: " + posSystemId + ", " +
                    "transaction number: " + transactionNumber);
        }
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setPosSystemId(posSystemId);
        transaction.setTransactionNumber(transactionNumber);
        transaction.setTimeCreated(LocalDateTime.now());
        transactionRepository.saveTransaction(transaction);
        if (Application.DEBUG) {
            System.out.println("[TransactionService] Created transaction: " + transaction);
        }
        return transaction;
    }

    /**
     * Save a transaction. The transaction should already exist in the repository.
     *
     * @param transaction the transaction to save
     */
    public void saveTransaction(@NonNull Transaction transaction) {
        transactionRepository.saveTransaction(transaction);
    }

    /**
     * Add an item to a transaction. If the item is already in the transaction, the quantity of the line item is
     * incremented by one. If the item is not in the transaction, a new line item is created with a quantity of one.
     *
     * @param transaction the transaction to add the item to
     * @param itemUpc     the UPC of the item to add
     */
    public boolean addItemToTransaction(@NonNull Transaction transaction, @NonNull String itemUpc) {
        List<LineItem> lineItems = transaction.getLineItems();
        LineItem lineItem = null;
        for (LineItem item : lineItems) {
            if (item.getItemUpc().equals(itemUpc) && !item.isVoided()) {
                lineItem = item;
                break;
            }
        }
        if (lineItem == null) {
            lineItem = new LineItem();
            lineItem.setItemUpc(itemUpc);
            lineItem.setTransactionId(transaction.getId());
            lineItems.add(lineItem);
        }
        lineItem.setQuantity(lineItem.getQuantity() + 1);
        transaction.setLineItems(lineItems);
        saveTransaction(transaction);
        if (Application.DEBUG) {
            System.out.println("[TransactionService] Added item with UPC " + itemUpc + " to transaction: " + transaction);
        }
        return true;
    }

    /**
     * Remove an item from a transaction. If the item is in the transaction, the quantity of the line item is
     * decremented by one. If the quantity of the line item is zero, the line item is removed from the transaction.
     *
     * @param transaction the transaction to remove the item from
     * @param itemUpc     the UPC of the item to remove
     * @return true if the item was removed, false if the item was not removed
     */
    public boolean removeItemFromTransaction(@NonNull Transaction transaction, @NonNull String itemUpc) {
        List<LineItem> lineItems = transaction.getLineItems();
        LineItem lineItem = null;
        for (LineItem item : lineItems) {
            if (item.getItemUpc().equals(itemUpc) && !item.isVoided()) {
                lineItem = item;
                break;
            }
        }
        boolean decremented = false;
        if (lineItem != null) {
            int newQuantity = lineItem.getQuantity() - 1;
            if (newQuantity <= 0) {
                lineItem.setQuantity(1);
                if (Application.DEBUG) {
                    System.err.println("[TransactionService] Attempted to remove item with UPC " + itemUpc + ", but " +
                            "quantity cannot be less than one. To remove the item, void the line item instead.");
                }
            } else {
                lineItem.setQuantity(newQuantity);
                if (Application.DEBUG) {
                    System.out.println("[TransactionService] Removed item with UPC " + itemUpc + " from transaction: "
                            + transaction);
                }
                decremented = true;
            }
            transaction.setLineItems(lineItems);
            saveTransaction(transaction);
        }
        return decremented;
    }

    /**
     * Void a line item in a transaction. If the line item is in the transaction and has not already been voided, it
     * is voided.
     *
     * @param transaction the transaction to void the line item in
     * @param itemUpc     the UPC of the line item to void
     */
    public boolean voidLineItemInTransaction(@NonNull Transaction transaction, @NonNull String itemUpc) {
        List<LineItem> lineItems = transaction.getLineItems();
        LineItem lineItem = null;
        for (LineItem item : lineItems) {
            if (item.getItemUpc().equals(itemUpc) && !item.isVoided()) {
                lineItem = item;
                break;
            }
        }
        if (lineItem != null) {
            lineItem.setVoided(true);
            transaction.setLineItems(lineItems);
            saveTransaction(transaction);
            if (Application.DEBUG) {
                System.out.println("[TransactionService] Voided line item with UPC " + itemUpc + " in transaction: " + transaction);
            }
        }
        return true;
    }
}
