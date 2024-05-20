package com.rocketpartners.onboarding.possystem.repository;

import com.rocketpartners.onboarding.possystem.model.Transaction;

import java.util.List;

/**
 * The {@code TransactionRepository} interface provides methods for performing CRUD operations
 * on {@link Transaction} objects. It defines methods for saving, retrieving, and deleting transactions,
 * as well as methods for querying transactions based on customer and POS system IDs.
 */
public interface TransactionRepository {

    /**
     * Saves the given {@code Transaction} to the repository.
     *
     * @param transaction the transaction to be saved
     */
    void saveTransaction(Transaction transaction);

    /**
     * Retrieves the {@code Transaction} with the specified ID from the repository.
     *
     * @param id the ID of the transaction to be retrieved
     * @return the transaction with the specified ID, or {@code null} if no such transaction exists
     */
    Transaction getTransactionById(String id);

    /**
     * Deletes the {@code Transaction} with the specified ID from the repository.
     *
     * @param id the ID of the transaction to be deleted
     */
    void deleteTransactionById(String id);

    /**
     * Checks whether a {@code Transaction} with the specified ID exists in the repository.
     *
     * @param id the ID of the transaction to check for
     * @return {@code true} if a transaction with the specified ID exists, {@code false} otherwise
     */
    boolean transactionExists(String id);

    /**
     * Retrieves a list of {@code Transaction} objects associated with the specified customer ID.
     *
     * @param customerId the ID of the customer whose transactions are to be retrieved
     * @return a list of transactions associated with the specified customer ID
     */
    List<Transaction> getTransactionsByCustomerId(String customerId);

    /**
     * Retrieves a list of {@code Transaction} objects associated with the specified POS system ID.
     *
     * @param posSystemId the ID of the POS system whose transactions are to be retrieved
     * @return a list of transactions associated with the specified POS system ID
     */
    List<Transaction> getTransactionsByPosSystemId(String posSystemId);
}

