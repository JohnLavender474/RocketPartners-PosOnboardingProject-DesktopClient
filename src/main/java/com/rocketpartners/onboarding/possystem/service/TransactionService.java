package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.commons.model.Item;
import com.rocketpartners.onboarding.commons.model.LineItem;
import com.rocketpartners.onboarding.commons.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service class for Transaction objects. This class provides methods for creating, saving, and modifying transactions.
 * It also provides methods for computing the subtotal, taxes, discounts, and total for a transaction.
 */
@SuppressWarnings("DuplicatedCode")
@ToString
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final DiscountService discountService;
    private final ItemService itemService;
    private final TaxService taxService;

    /**
     * Create a new Transaction object and persist it.
     *
     * @param posSystemId       the ID of the POS system
     * @param transactionNumber the transaction number
     * @return the created and persisted Transaction object
     */
    public Transaction createAndPersist(@NonNull String posSystemId, int transactionNumber) {
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
     * Recompute the subtotal, taxes, discounts, and total for a transaction. After the transaction is recomputed, the
     * transaction is saved.
     *
     * @param transaction the transaction to recompute
     */
    public void recomputeAndSaveTransaction(@NonNull Transaction transaction) {
        transaction.setSubtotal(BigDecimal.ZERO);

        transaction.getLineItems().stream().filter(it -> !it.isVoided()).forEach(lineItem -> {
            Item item = itemService.getItemByUpc(lineItem.getItemUpc());
            if (item != null) {
                BigDecimal itemPrice = item.getUnitPrice();
                BigDecimal lineItemSubtotal = itemPrice.multiply(BigDecimal.valueOf(lineItem.getQuantity()));
                BigDecimal currentSubtotal = transaction.getSubtotal();
                transaction.setSubtotal(currentSubtotal.add(lineItemSubtotal));
            }
        });

        BigDecimal subtotal = transaction.getSubtotal();

        BigDecimal taxes = taxService.computeTaxesFor(transaction);
        transaction.setTaxes(taxes);

        BigDecimal discountAmount = discountService.computeDiscountAmountToApplyTo(transaction);
        transaction.setDiscountAmount(discountAmount);

        BigDecimal total = BigDecimal.ZERO;
        total = total.add(subtotal);
        total = total.add(taxes);
        total = total.subtract(discountAmount);
        transaction.setTotal(total);

        saveTransaction(transaction);
    }

    /**
     * Save a transaction. The transaction should already exist in the repository. To recompute the transaction before
     * saving it, use {@link #recomputeAndSaveTransaction(Transaction)} instead.
     *
     * @param transaction the transaction to save
     */
    public void saveTransaction(@NonNull Transaction transaction) {
        transactionRepository.saveTransaction(transaction);
    }

    /**
     * Add an item to a transaction. If the item is already in the transaction, the quantity of the line item is
     * incremented by one. If the item is not in the transaction, a new line item is created with a quantity of one.
     * After the item is added, the transaction is recomputed and saved via
     * {@link #recomputeAndSaveTransaction(Transaction)}.
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

        recomputeAndSaveTransaction(transaction);

        if (Application.DEBUG) {
            System.out.println("[TransactionService] Added item with UPC " + itemUpc + " to transaction: " + transaction);
        }

        return true;
    }

    /**
     * Remove one item from a transaction. If the item is in the transaction, the quantity of the line item is
     * decremented by one. The quantity of the line item cannot be less than one. If the quantity would be less than
     * one, the quantity is set to one. To completely remove an item from a transaction, void the line item instead.
     * After the item is removed, the transaction is recomputed and saved via
     * {@link #recomputeAndSaveTransaction(Transaction)}.
     *
     * @param transaction the transaction to remove the item from
     * @param itemUpc     the UPC of the item to remove
     * @return true if the item was removed, false if the item was not removed
     */
    public boolean removeItemFromTransaction(@NonNull Transaction transaction, @NonNull String itemUpc) {
        List<LineItem> lineItems = transaction.getLineItems();
        LineItem foundLineItem = null;
        for (LineItem lineItem : lineItems) {
            if (itemUpc.equals(lineItem.getItemUpc()) && !lineItem.isVoided()) {
                foundLineItem = lineItem;
                break;
            }
        }

        boolean decremented = false;
        if (foundLineItem != null) {
            int newQuantity = foundLineItem.getQuantity() - 1;
            if (newQuantity <= 0) {
                foundLineItem.setQuantity(1);
                if (Application.DEBUG) {
                    System.err.println("[TransactionService] Attempted to remove item with UPC " + itemUpc + ", but " +
                            "quantity cannot be less than one. To remove the item, void the line item instead.");
                }
            } else {
                foundLineItem.setQuantity(newQuantity);
                if (Application.DEBUG) {
                    System.out.println("[TransactionService] Removed item with UPC " + itemUpc + " from transaction: "
                            + transaction);
                }
                decremented = true;
            }
            transaction.setLineItems(lineItems);
            recomputeAndSaveTransaction(transaction);
        }

        return decremented;
    }

    /**
     * Void a line item in a transaction. If the line item is in the transaction and has not already been voided, it
     * is voided. After the line item is voided, the transaction is recomputed and saved via
     * {@link #recomputeAndSaveTransaction(Transaction)}.
     *
     * @param transaction the transaction to void the line item in
     * @param itemUpc     the UPC of the line item to void
     */
    public void voidLineItemInTransaction(@NonNull Transaction transaction, @NonNull String itemUpc) {
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
            recomputeAndSaveTransaction(transaction);
            if (Application.DEBUG) {
                System.out.println("[TransactionService] Voided line item with UPC " + itemUpc + " in transaction: " + transaction);
            }
        }
    }
}
