package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;

/**
 * Factory class for creating and persisting new POS system objects.
 */
public class PosSystemService {

    private final PosSystemRepository posSystemRepository;

    /**
     * Constructor that accepts a POS system repository.
     *
     * @param posSystemRepository the POS system repository
     */
    public PosSystemService(PosSystemRepository posSystemRepository) {
        this.posSystemRepository = posSystemRepository;
    }

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
}
