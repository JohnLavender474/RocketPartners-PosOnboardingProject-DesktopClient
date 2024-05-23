package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.repository.ItemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * Create a new Item object and persist it. The UPC, name, and unit price are required fields. The category and
     * description are optional.
     *
     * @param upc         the UPC of the item
     * @param name        the name of the item
     * @param unitPrice   the unit price of the item
     * @param category    the category of the item
     * @param description the description of the item
     * @return the created and persisted Item object
     * @throws IllegalArgumentException if the UPC, name, or unit price is null or empty, or if the price is less
     *                                  than 0, or if an item with the same UPC already exists
     */
    public Item createAndPersist(@NonNull String upc, @NonNull String name, @NonNull BigDecimal unitPrice,
                                 String category, String description) {
        if (upc.isEmpty()) {
            throw new IllegalArgumentException("UPC cannot be null or empty");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (unitPrice.doubleValue() < 0) {
            throw new IllegalArgumentException("Price must be greater than or equal to 0");
        }
        if (itemRepository.itemExists(upc)) {
            throw new IllegalArgumentException("Item with UPC " + upc + " already exists");
        }
        Item item = new Item();
        item.setUpc(upc);
        item.setName(name);
        item.setUnitPrice(unitPrice);
        item.setCategory(category);
        item.setDescription(description);
        saveItem(item);
        return item;
    }

    /**
     * Save an existing Item object. The item must already exist in the repository.
     *
     * @param item the item to be saved
     */
    public void saveItem(Item item) {
        itemRepository.saveItem(item);
    }

    /**
     * Retrieve the Item with the specified UPC from the repository.
     *
     * @param upc the UPC of the item to be retrieved
     * @return the item with the specified UPC, or null if no such item exists
     */
    public Item getItemByUpc(String upc) {
        return itemRepository.getItemByUpc(upc);
    }

    /**
     * Retrieve all Item objects from the repository.
     *
     * @return a list of all items in the repository
     */
    public List<Item> getAllItems() {
        return itemRepository.getAllItems();
    }

    /**
     * Delete the Item with the specified UPC from the repository.
     *
     * @param upc the UPC of the item to be deleted
     */
    public void deleteItemByUpc(String upc) {
        itemRepository.deleteItemByUpc(upc);
    }

    /**
     * Retrieve a list of Item objects from the repository that have the specified name.
     *
     * @param name the name of the items to be retrieved
     * @return a list of items with the specified name
     */
    public List<Item> getItemsByName(String name) {
        return itemRepository.getItemsByName(name);
    }

    /**
     * Retrieve a list of Item objects from the repository that belong to the specified category.
     *
     * @param category the category of the items to be retrieved
     * @return a list of items with the specified category
     */
    public List<Item> getItemsByCategory(String category) {
        return itemRepository.getItemsByCategory(category);
    }

    /**
     * Check whether an Item with the specified UPC exists in the repository.
     *
     * @param upc the UPC of the item to check for
     * @return true if an item with the specified UPC exists, false otherwise
     */
    public boolean itemExists(String upc) {
        return itemRepository.itemExists(upc);
    }

    /**
     * Retrieve a list of random Item objects from the repository. The number of items returned is limited by the max
     * parameter. If the number of items in the repository is less than max, all available items will be returned. The
     * order of the items is randomized.
     *
     * @param max the maximum number of items to return
     * @return a list of random items
     */
    public List<Item> getRandomItems(int max) {
        return getRandomItemsNotIn(Set.of(), max);
    }

    /**
     * Retrieve a list of random Item objects from the repository that are not contained in the provided list of UPCs.
     * The number of items returned is limited by the max parameter. If the number of items not contained in the list of
     * UPCs is less than max, all available items will be returned. The order of the items is randomized.
     *
     * @param itemUpcs the list of UPCs to exclude
     * @param max      the maximum number of items to return
     * @return a list of random items not contained in the provided list of UPCs
     */
    public List<Item> getRandomItemsNotIn(Set<String> itemUpcs, int max) {
        List<Item> allItems = itemRepository.getAllItems();
        Collections.shuffle(allItems);
        max = Math.min(max, allItems.size());
        List<Item> itemsToReturn = new ArrayList<>();
        for (Item item : allItems) {
            if (itemUpcs.contains(item.getUpc())) {
                continue;
            }
            itemsToReturn.add(item);
            if (itemsToReturn.size() >= max) {
                break;
            }
        }
        return itemsToReturn;
    }
}
