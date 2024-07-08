package com.rocketpartners.onboarding.possystem.repository;

import com.rocketpartners.onboarding.commons.model.PosSystem;

import java.util.List;

/**
 * The {@code PosSystemRepository} interface provides methods for performing CRUD operations
 * on {@link PosSystem} objects. It defines methods for saving, retrieving, and deleting POS systems,
 * as well as methods for querying POS systems based on their attributes.
 */
public interface PosSystemRepository {

    /**
     * Saves the given {@code PosSystem} to the repository.
     *
     * @param posSystem the POS system to be saved
     */
    void savePosSystem(PosSystem posSystem);

    /**
     * Retrieves the {@code PosSystem} with the specified ID from the repository.
     *
     * @param id the ID of the POS system to be retrieved
     * @return the POS system with the specified ID, or {@code null} if no such POS system exists
     */
    PosSystem getPosSystemById(String id);

    /**
     * Deletes the {@code PosSystem} with the specified ID from the repository.
     *
     * @param id the ID of the POS system to be deleted
     */
    void deletePosSystemById(String id);

    /**
     * Checks whether a {@code PosSystem} with the specified ID exists in the repository.
     *
     * @param id the ID of the POS system to check for
     * @return {@code true} if a POS system with the specified ID exists, {@code false} otherwise
     */
    boolean posSystemExists(String id);

    /**
     * Retrieves all {@code PosSystem} objects from the repository.
     *
     * @return a list of all POS systems in the repository
     */
    List<PosSystem> getAllPosSystems();

    /**
     * Retrieves a list of {@code PosSystem} objects from the repository that are associated with the specified store name.
     *
     * @param storeName the name of the store to which the POS systems are associated
     * @return a list of POS systems associated with the specified store name
     */
    List<PosSystem> getPosSystemsByStoreName(String storeName);

    /**
     * Retrieves the {@code PosSystem} with the specified store name and POS lane from the repository.
     *
     * @param storeName the name of the store
     * @param posLane the lane of the POS system
     * @return the POS system with the specified store name and POS lane, or {@code null} if no such POS system exists
     */
    PosSystem getPosSystemByStoreNameAndPosLane(String storeName, int posLane);

    /**
     * Checks whether a {@code PosSystem} with the specified store name and POS lane exists in the repository.
     *
     * @param storeName the name of the store
     * @param posLane the lane of the POS system
     * @return {@code true} if a POS system with the specified store name and POS lane exists, {@code false} otherwise
     */
    boolean posSystemExistsByStoreNameAndPosLane(String storeName, int posLane);
}

