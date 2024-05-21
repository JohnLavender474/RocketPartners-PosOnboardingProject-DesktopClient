package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.possystem.model.LineItem;
import com.rocketpartners.onboarding.possystem.repository.LineItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of the {@link LineItemRepository} interface.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryLineItemRepository implements LineItemRepository {

    private static InMemoryLineItemRepository instance;

    private final Map<String, LineItem> lineItems = new HashMap<>();

    /**
     * Get the singleton instance of the in-memory repository.
     *
     * @return The instance of the in-memory repository.
     */
    public static InMemoryLineItemRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryLineItemRepository();
        }
        return instance;
    }

    public String buildKey(LineItem item) {
        return buildKey(item.getTransactionId(), item.getItemUpc());
    }

    public String buildKey(String transactionId, String itemUpc) {
        return transactionId + "_" + itemUpc;
    }

    @Override
    public void saveLineItem(LineItem lineItem) {
        String key = buildKey(lineItem);
        lineItems.put(key, lineItem);
    }

    @Override
    public List<LineItem> getAllLineItems() {
        return lineItems.values().stream().toList();
    }

    @Override
    public void deleteLineItemByTransactionIdAndItemUpc(String transactionId, String itemUpc) {
        String key = buildKey(transactionId, itemUpc);
        lineItems.remove(key);
    }

    @Override
    public LineItem getLineItemByTransactionIdAndItemUpc(String transactionId, String itemUpc) {
        String key = buildKey(transactionId, itemUpc);
        return lineItems.get(key);
    }

    @Override
    public boolean lineItemExistsWithTransactionIdAndItemUpc(String transactionId, String itemUpc) {
        String key = buildKey(transactionId, itemUpc);
        return lineItems.containsKey(key);
    }

    @Override
    public List<LineItem> getLineItemsByTransactionId(String transactionId) {
        return lineItems.values().stream()
                .filter(lineItem -> lineItem.getTransactionId().equals(transactionId))
                .toList();
    }
}
