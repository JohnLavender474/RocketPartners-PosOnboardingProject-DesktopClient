package com.rocketpartners.onboarding.possystem.factory;

import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;

/**
 * Factory class for creating new Transaction objects.
 */
public class TransactionFactory {

    private final TransactionRepository transactionRepository;

    /**
     * Constructor that accepts a transaction repository.
     *
     * @param transactionRepository the transaction repository
     */
    public TransactionFactory(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Create a new Transaction object and persist it.
     *
     * @param posSystemId       the ID of the POS system
     * @param transactionNumber the transaction number
     * @return the created and persisted Transaction object
     */
    public Transaction createAndPersist(String posSystemId, int transactionNumber) {
        Transaction transaction = new Transaction();
        transaction.setPosSystemId(posSystemId);
        transaction.setTransactionNumber(transactionNumber);
        transactionRepository.saveTransaction(transaction);
        return transaction;
    }
}
