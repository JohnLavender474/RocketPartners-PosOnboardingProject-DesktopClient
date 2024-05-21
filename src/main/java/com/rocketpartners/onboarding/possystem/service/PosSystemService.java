package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Service class for managing POS system objects.
 */
@RequiredArgsConstructor
public class PosSystemService {

    private final PosSystemRepository posSystemRepository;

    /**
     * Create a new POS system object and persist it.
     *
     * @param storeName the name of the store
     * @param posLane   the POS lane number
     * @return the created and persisted POS system object
     * @throws IllegalArgumentException if a POS system already exists for the store name and POS lane
     */
    public PosSystem createAndPersist(String storeName, int posLane) {
        if (posSystemRepository.posSystemExistsByStoreNameAndPosLane(storeName, posLane)) {
            throw new IllegalArgumentException("POS system already exists for store name and POS lane");
        }
        PosSystem posSystem = new PosSystem();
        posSystem.setStoreName(storeName);
        posSystem.setPosLane(posLane);
        posSystemRepository.savePosSystem(posSystem);
        return posSystem;
    }

    /**
     * Save the given POS system to the repository.
     *
     * @param posSystem the POS system to be saved
     */
    public void savePosSystem(PosSystem posSystem) {
        posSystemRepository.savePosSystem(posSystem);
    }

    /**
     * Retrieves the POS system with the specified ID from the repository.
     *
     * @param posSystemId the ID of the POS system to be retrieved
     * @return the POS system with the specified ID, or {@code null} if no such POS system exists
     */
    public PosSystem getPosSystemById(String posSystemId) {
        return posSystemRepository.getPosSystemById(posSystemId);
    }

    /**
     * Deletes the POS system with the specified ID from the repository.
     *
     * @param posSystemId the ID of the POS system to be deleted
     */
    public void deletePosSystemById(String posSystemId) {
        posSystemRepository.deletePosSystemById(posSystemId);
    }

    /**
     * Checks whether a POS system with the specified ID exists in the repository.
     *
     * @param posSystemId the ID of the POS system to check for
     * @return {@code true} if a POS system with the specified ID exists, {@code false} otherwise
     */
    public boolean posSystemExists(String posSystemId) {
        return posSystemRepository.posSystemExists(posSystemId);
    }

    /**
     * Retrieves all POS system objects from the repository.
     *
     * @return a list of all POS systems in the repository
     */
    public List<PosSystem> getAllPosSystems() {
        return posSystemRepository.getAllPosSystems();
    }

    /**
     * Retrieves a list of POS system objects from the repository that are associated with the specified store name.
     *
     * @param storeName the name of the store to which the POS systems are associated
     * @return a list of POS systems associated with the specified store name
     */
    public List<PosSystem> getPosSystemsByStoreName(String storeName) {
        return posSystemRepository.getPosSystemsByStoreName(storeName);
    }

    /**
     * Retrieves the POS system with the specified store name and POS lane from the repository.
     *
     * @param storeName the name of the store
     * @param posLane   the lane of the POS system
     * @return the POS system with the specified store name and POS lane, or {@code null} if no such POS system exists
     */
    public PosSystem getPosSystemByStoreNameAndPosLane(String storeName, int posLane) {
        return posSystemRepository.getPosSystemByStoreNameAndPosLane(storeName, posLane);
    }

    /**
     * Checks whether a POS system with the specified store name and POS lane exists in the repository.
     *
     * @param storeName the name of the store
     * @param posLane   the lane of the POS system
     * @return {@code true} if a POS system with the specified store name and POS lane exists, {@code false} otherwise
     */
    public boolean posSystemExistsByStoreNameAndPosLane(String storeName, int posLane) {
        return posSystemRepository.posSystemExistsByStoreNameAndPosLane(storeName, posLane);
    }
}
