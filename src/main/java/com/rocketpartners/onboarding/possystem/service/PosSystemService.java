package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Service class for managing POS system objects.
 */
@ToString
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
    public PosSystem createAndPersist(@NonNull String storeName, int posLane) {
        if (Application.DEBUG) {
            System.out.println("[PosSystemService] Creating POS system for store name: " + storeName + ", POS lane: " + posLane);
        }
        if (posSystemRepository.posSystemExistsByStoreNameAndPosLane(storeName, posLane)) {
            throw new IllegalArgumentException("POS system already exists for store name and POS lane");
        }
        PosSystem posSystem = new PosSystem();
        posSystem.setStoreName(storeName);
        posSystem.setPosLane(posLane);
        savePosSystem(posSystem);
        if (Application.DEBUG) {
            System.out.println("[PosSystemService] Created POS system: " + posSystem);
        }
        return posSystem;
    }

    /**
     * Save the given POS system to the repository.
     *
     * @param posSystem the POS system to be saved
     */
    public void savePosSystem(PosSystem posSystem) {
        posSystemRepository.savePosSystem(posSystem);
        if (Application.DEBUG) {
            System.out.println("[PosSystemService] Saved POS system: " + posSystem);
        }
    }

    /**
     * Retrieves the POS system with the specified ID from the repository.
     *
     * @param posSystemId the ID of the POS system to be retrieved
     * @return the POS system with the specified ID, or {@code null} if no such POS system exists
     */
    public PosSystem getPosSystemById(String posSystemId) {
        PosSystem posSystem = posSystemRepository.getPosSystemById(posSystemId);
        if (Application.DEBUG) {
            System.out.println("Retrieved POS system: " + posSystem);
        }
        return posSystem;
    }

    /**
     * Deletes the POS system with the specified ID from the repository.
     *
     * @param posSystemId the ID of the POS system to be deleted
     */
    public void deletePosSystemById(String posSystemId) {
        if (Application.DEBUG) {
            System.out.println("Deleting POS system with ID: " + posSystemId);
        }
        posSystemRepository.deletePosSystemById(posSystemId);
    }

    /**
     * Checks whether a POS system with the specified ID exists in the repository.
     *
     * @param posSystemId the ID of the POS system to check for
     * @return {@code true} if a POS system with the specified ID exists, {@code false} otherwise
     */
    public boolean posSystemExists(String posSystemId) {
        boolean exists = posSystemRepository.posSystemExists(posSystemId);
        if (Application.DEBUG) {
            System.out.println("POS system exists with ID: " + posSystemId + ", " + exists);
        }
        return exists;
    }

    /**
     * Retrieves all POS system objects from the repository.
     *
     * @return a list of all POS systems in the repository
     */
    public List<PosSystem> getAllPosSystems() {
        List<PosSystem> posSystems = posSystemRepository.getAllPosSystems();
        if (Application.DEBUG) {
            System.out.println("Retrieving all POS systems: " + posSystems);
        }
        return posSystems;
    }

    /**
     * Retrieves a list of POS system objects from the repository that are associated with the specified store name.
     *
     * @param storeName the name of the store to which the POS systems are associated
     * @return a list of POS systems associated with the specified store name
     */
    public List<PosSystem> getPosSystemsByStoreName(String storeName) {
        List<PosSystem> posSystems = posSystemRepository.getPosSystemsByStoreName(storeName);
        if (Application.DEBUG) {
            System.out.println("Retrieved POS systems for store name: " + storeName);
        }
        return posSystems;
    }

    /**
     * Retrieves the POS system with the specified store name and POS lane from the repository.
     *
     * @param storeName the name of the store
     * @param posLane   the lane of the POS system
     * @return the POS system with the specified store name and POS lane, or {@code null} if no such POS system exists
     */
    public PosSystem getPosSystemByStoreNameAndPosLane(String storeName, int posLane) {
        PosSystem posSystem = posSystemRepository.getPosSystemByStoreNameAndPosLane(storeName, posLane);
        if (Application.DEBUG) {
            System.out.println("Retrieved POS system for store name: " + storeName + ", POS lane: " + posLane);
        }
        return posSystem;
    }

    /**
     * Checks whether a POS system with the specified store name and POS lane exists in the repository.
     *
     * @param storeName the name of the store
     * @param posLane   the lane of the POS system
     * @return {@code true} if a POS system with the specified store name and POS lane exists, {@code false} otherwise
     */
    public boolean posSystemExistsByStoreNameAndPosLane(String storeName, int posLane) {
        boolean exists = posSystemRepository.posSystemExistsByStoreNameAndPosLane(storeName, posLane);
        if (Application.DEBUG) {
            System.out.println("POS system exists for store name: " + storeName + ", POS lane: " + posLane + ", " + exists);
        }
        return exists;
    }
}
