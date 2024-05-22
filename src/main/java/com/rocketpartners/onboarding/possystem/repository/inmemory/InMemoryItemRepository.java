package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.repository.ItemRepository;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of the {@link ItemRepository} interface.
 */
@ToString
public class InMemoryItemRepository implements ItemRepository {

    private final Map<String, Item> items = new HashMap<>();

    @Override
    public void saveItem(Item item) {
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
