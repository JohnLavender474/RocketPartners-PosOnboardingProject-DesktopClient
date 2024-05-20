package com.rocketpartners.onboarding.possystem.factory;

import com.rocketpartners.onboarding.possystem.model.Transaction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Factory class for creating new Transaction objects.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionFactory {

    private static TransactionFactory instance;

    public static TransactionFactory getInstance() {
        if (instance == null) {
            instance = new TransactionFactory();
        }
        return instance;
    }

    public Transaction createTransaction(String posSystemId, int transactionNumber) {
        Transaction transaction = new Transaction();
        transaction.setPosSystemId(posSystemId);
        transaction.setTransactionNumber(transactionNumber);
        return transaction;
    }
}
