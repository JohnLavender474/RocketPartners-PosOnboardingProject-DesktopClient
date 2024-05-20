package com.rocketpartners.onboarding.possystem.repository;

import com.rocketpartners.onboarding.possystem.model.Item;

import java.util.List;

/**
 * The {@code ItemRepository} interface provides methods for performing CRUD operations
 * on {@link Item} objects. It defines methods for saving, retrieving, and deleting items,
 * as well as methods for querying items based on their attributes.
 */
public interface ItemRepository {

    /**
     * Saves the given {@code Item} to the repository.
     *
     * @param item the item to be saved
     */
    void saveItem(Item item);

    /**
     * Retrieves all {@code Item} objects from the repository.
     *
     * @return a list of all items in the repository
     */
    List<Item> getAllItems();

    /**
     * Deletes the given {@code Item} from the repository.
     *
     * @param item the item to be deleted
     */
    void deleteItem(Item item);

    /**
     * Deletes the {@code Item} with the specified UPC from the repository.
     *
     * @param upc the UPC of the item to be deleted
     */
    void deleteItemByUpc(String upc);

    /**
     * Retrieves the {@code Item} with the specified UPC from the repository.
     *
     * @param upc the UPC of the item to be retrieved
     * @return the item with the specified UPC, or {@code null} if no such item exists
     */
    Item getItemByUpc(String upc);

    /**
     * Checks whether an {@code Item} with the specified UPC exists in the repository.
     *
     * @param upc the UPC of the item to check for
     * @return {@code true} if an item with the specified UPC exists, {@code false} otherwise
     */
    boolean itemExists(String upc);

    /**
     * Retrieves a list of {@code Item} objects from the repository that have the specified name.
     *
     * @param name the name of the items to be retrieved
     * @return a list of items with the specified name
     */
    List<Item> getItemsByName(String name);

    /**
     * Retrieves a list of {@code Item} objects from the repository that belong to the specified category.
     *
     * @param category the category of the items to be retrieved
     * @return a list of items with the specified category
     */
    List<Item> getItemsByCategory(String category);
}

