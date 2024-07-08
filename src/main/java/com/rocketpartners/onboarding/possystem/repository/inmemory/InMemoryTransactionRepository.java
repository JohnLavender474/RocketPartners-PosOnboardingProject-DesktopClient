package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.commons.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import lombok.NonNull;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of the {@link TransactionRepository} interface.
 */
@ToString
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<String, Transaction> transactions = new HashMap<>();

    @Override
    public void saveTransaction(@NonNull Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public Transaction getTransactionById(@NonNull String id) {
        return transactions.get(id);
    }

    @Override
    public void deleteTransactionById(@NonNull String id) {
        transactions.remove(id);
    }

    @Override
    public boolean transactionExists(@NonNull String id) {
        return transactions.containsKey(id);
    }

    @Override
    public List<Transaction> getTransactionsByCustomerId(@NonNull String customerId) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getCustomerId().equals(customerId))
                .toList();
    }

    @Override
    public List<Transaction> getTransactionsByPosSystemId(@NonNull String posSystemId) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getPosSystemId().equals(posSystemId))
                .toList();
    }
}
