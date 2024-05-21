package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;

import java.time.LocalDateTime;

/**
 * Factory class for creating new Transaction objects.
 */
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Constructor that accepts a transaction repository.
     *
     * @param transactionRepository the transaction repository
     */
    public TransactionService(TransactionRepository transactionRepository) {
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
        transaction.setTimeCreated(LocalDateTime.now());
        transactionRepository.saveTransaction(transaction);
        return transaction;
    }
}
