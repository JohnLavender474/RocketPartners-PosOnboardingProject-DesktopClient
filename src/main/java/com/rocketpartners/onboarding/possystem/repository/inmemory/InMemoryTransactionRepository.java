package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An in-memory implementation of the {@link TransactionRepository} interface.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryTransactionRepository implements TransactionRepository {

    private static InMemoryTransactionRepository instance;

    private final Map<String, Transaction> transactions = new HashMap<>();

    /**
     * Get the singleton instance of the in-memory repository.
     *
     * @return The instance of the in-memory repository.
     */
    public static InMemoryTransactionRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryTransactionRepository();
        }
        return instance;
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        if (transaction.getId() == null) {
            String id = UUID.randomUUID().toString();
            transaction.setId(String.valueOf(id));
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
