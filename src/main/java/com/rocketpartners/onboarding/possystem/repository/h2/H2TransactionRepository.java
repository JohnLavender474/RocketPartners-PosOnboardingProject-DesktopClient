package com.rocketpartners.onboarding.possystem.repository.h2;

import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;

import java.util.List;

public class H2TransactionRepository implements TransactionRepository {

    @Override
    public void saveTransaction(Transaction transaction) {

    }

    @Override
    public Transaction getTransactionById(String id) {
        return null;
    }

    @Override
    public void deleteTransaction(Transaction transaction) {

    }

    @Override
    public void deleteTransactionById(String id) {

    }

    @Override
    public boolean transactionExists(String id) {
        return false;
    }

    @Override
    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        return List.of();
    }

    @Override
    public List<Transaction> getTransactionsByPosSystemId(String posSystemId) {
        return List.of();
    }
}
