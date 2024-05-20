package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of the {@link TransactionRepository} interface.
 */
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<String, Transaction> transactions = new HashMap<>();

    @Override
    public void saveTransaction(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.setId(String.valueOf(transactions.size() + 1));
        }
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public Transaction getTransactionById(String id) {
        return transactions.get(id);
    }

    @Override
    public void deleteTransactionById(String id) {
        transactions.remove(id);
    }

    @Override
    public boolean transactionExists(String id) {
        return transactions.containsKey(id);
    }

    @Override
    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getCustomerId().equals(customerId))
                .toList();
    }

    @Override
    public List<Transaction> getTransactionsByPosSystemId(String posSystemId) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getPosSystemId().equals(posSystemId))
                .toList();
    }
}
