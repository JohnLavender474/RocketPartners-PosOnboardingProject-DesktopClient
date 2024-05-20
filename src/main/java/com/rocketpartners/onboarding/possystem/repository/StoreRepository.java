package com.rocketpartners.onboarding.possystem.repository;

import com.rocketpartners.onboarding.possystem.model.Store;

/**
 * Repository for stores. This interface defines the methods that must be implemented by any class that
 * provides access to store data. It includes methods for saving, retrieving, and deleting stores, as well
 * as methods for querying stores based on their attributes.
 */
public interface StoreRepository {

    /**
     * Saves the given {@code Store} to the repository.
     *
     * @param store the store to be saved
     */
    void saveStore(Store store);

    /**
     * Retrieves all {@code Store} objects from the repository.
     *
     * @param id the id of the store to be retrieved
     * @return a list of all stores in the repository
     */
    Store getStoreById(String id);

    /**
     * Deletes the {@code Store} with the specified ID from the repository.
     *
     * @param id the ID of the store to be deleted
     */
    void deleteStoreById(String id);

    /**
     * Retrieves the {@code Store} with the specified ID from the repository.
     *
     * @param id the ID of the store to be retrieved
     * @return the store with the specified ID, or {@code null} if no such store exists
     */
    boolean storeExists(String id);

    /**
     * Retrieves the {@code Store} with the specified name from the repository.
     *
     * @param name the name of the store to be retrieved
     * @return the store with the specified name, or {@code null} if no such store exists
     */
    Store getStoreByName(String name);

    /**
     * Checks whether a {@code Store} with the specified name exists in the repository.
     *
     * @param name the name of the store to check for
     * @return {@code true} if a store with the specified name exists, {@code false} otherwise
     */
    boolean storeExistsByName(String name);

    /**
     * Retrieves the {@code Store} with the specified address from the repository.
     *
     * @param address the address of the store to be retrieved
     * @return the store with the specified address, or {@code null} if no such store exists
     */
    Store getStoreByAddress(String address);

    /**
     * Checks whether a {@code Store} with the specified address exists in the repository.
     *
     * @param address the address of the store to check for
     * @return {@code true} if a store with the specified address exists, {@code false} otherwise
     */
    boolean storeExistsByAddress(String address);
}
