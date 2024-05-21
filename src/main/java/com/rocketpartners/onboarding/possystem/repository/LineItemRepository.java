package com.rocketpartners.onboarding.possystem.repository;

import com.rocketpartners.onboarding.possystem.model.LineItem;

import java.util.List;

/**
 * The {@code LineItemRepository} interface provides methods for performing CRUD operations
 * on {@link LineItem} objects. It defines methods for saving, retrieving, and deleting line items,
 * as well as methods for querying line items based on their attributes.
 */
public interface LineItemRepository {

    /**
     * Saves the given {@code LineItem} to the repository.
     *
     * @param lineItem the line item to be saved
     */
    void saveLineItem(LineItem lineItem);

    /**
     * Retrieves all {@code LineItem} objects from the repository.
     *
     * @return a list of all line items in the repository
     */
    List<LineItem> getAllLineItems();

    /**
     * Deletes the {@code LineItem} with the specified transaction ID and item UPC from the repository.
     *
     * @param transactionId the ID of the transaction to which the line item is associated
     * @param itemUpc       the UPC of the item to be deleted
     */
    void deleteLineItemByTransactionIdAndItemUpc(String transactionId, String itemUpc);

    /**
     * Retrieves the {@code LineItem} with the specified transaction ID and item UPC from the repository.
     *
     * @param transactionId the ID of the transaction to which the line item is associated
     * @param itemUpc       the UPC of the item to be retrieved
     * @return the line item with the specified ID, or {@code null} if no such line item exists
     */
    LineItem getLineItemByTransactionIdAndItemUpc(String transactionId, String itemUpc);

    /**
     * Checks whether a {@code LineItem} with the specified transaction ID and item UPC exists in the repository.
     *
     * @param transactionId the ID of the transaction to which the line item is associated
     * @param itemUpc       the UPC of the item to check for
     * @return {@code true} if a line item with the specified ID exists, {@code false} otherwise
     */
    boolean lineItemExistsWithTransactionIdAndItemUpc(String transactionId, String itemUpc);

    /**
     * Retrieves a list of {@code LineItem} objects from the repository that are associated with the specified
     * transaction ID.
     *
     * @param transactionId the ID of the transaction to which the line items are associated
     * @return a list of line items associated with the specified transaction ID
     */
    List<LineItem> getLineItemsByTransactionId(String transactionId);
}

