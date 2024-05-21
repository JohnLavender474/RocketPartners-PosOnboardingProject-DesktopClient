package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.repository.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of the {@link ItemRepository} interface.
 */
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryItemRepository implements ItemRepository {

    private static InMemoryItemRepository instance;

    private final Map<String, Item> items = new HashMap<>();

    /**
     * Get the singleton instance of the in-memory repository.
     *
     * @return The instance of the in-memory repository.
     */
    public static InMemoryItemRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryItemRepository();
        }
        return instance;
    }

    @Override
    public void saveItem(Item item) {
        if (item.getUpc() == null) {
            throw new IllegalArgumentException("Item UPC cannot be null");
        }
        items.put(item.getUpc(), item);
    }

    @Override
    public List<Item> getAllItems() {
        return items.values().stream().toList();
    }

    @Override
    public void deleteItemByUpc(String upc) {
        items.remove(upc);
    }

    @Override
    public Item getItemByUpc(String upc) {
        return items.get(upc);
    }

    @Override
    public boolean itemExists(String upc) {
        return items.containsKey(upc);
    }

    @Override
    public List<Item> getItemsByName(String name) {
        return items.values().stream()
                .filter(item -> item.getName().equals(name))
                .toList();
    }

    @Override
    public List<Item> getItemsByCategory(String category) {
        return items.values().stream()
                .filter(item -> item.getCategory().equals(category))
                .toList();
    }
}
