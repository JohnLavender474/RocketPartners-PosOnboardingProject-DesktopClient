package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Service class for Transaction objects.
 */
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
            System.out.println("[TransactionService] Creating transaction for POS system ID: " + posSystemId + ", transaction number: " + transactionNumber);
        }
        Transaction transaction = new Transaction();
        transaction.setPosSystemId(posSystemId);
        transaction.setTransactionNumber(transactionNumber);
        transaction.setTimeCreated(LocalDateTime.now());
        transactionRepository.saveTransaction(transaction);
        if (Application.DEBUG) {
            System.out.println("[TransactionService] Created transaction: " + transaction);
        }
        return transaction;
    }
}
