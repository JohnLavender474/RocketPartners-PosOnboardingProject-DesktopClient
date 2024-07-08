package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.commons.model.Item;
import com.rocketpartners.onboarding.possystem.repository.ItemRepository;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
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
        return new ArrayList<>(items.values());
    }

    @Override
    public void deleteItemByUpc(@NonNull String upc) {
        items.remove(upc);
    }

    @Override
    public Item getItemByUpc(@NonNull String upc) {
        return items.get(upc);
    }

    @Override
    public boolean itemExists(@NonNull String upc) {
        return items.containsKey(upc);
    }

    @Override
    public List<Item> getItemsByName(@NonNull String name) {
        List<Item> items = new ArrayList<>();
        this.items.values().forEach(it -> {
            if (name.equals(it.getName())) {
                items.add(it);
            }
        });
        return items;
    }

    @Override
    public List<Item> getItemsByCategory(@NonNull String category) {
        List<Item> items = new ArrayList<>();
        this.items.values().forEach(it -> {
            if (category.equals(it.getCategory())) {
                items.add(it);
            }
        });
        return items;
    }
}
