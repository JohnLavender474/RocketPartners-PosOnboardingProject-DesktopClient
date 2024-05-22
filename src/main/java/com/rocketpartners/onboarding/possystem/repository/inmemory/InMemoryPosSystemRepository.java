package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An in-memory implementation of the {@link PosSystemRepository} interface.
 */
@ToString
public class InMemoryPosSystemRepository implements PosSystemRepository {

    private final Map<String, PosSystem> posSystems = new HashMap<>();

    @Override
    public void savePosSystem(PosSystem posSystem) {
        if (posSystem.getId() == null) {
            String id = UUID.randomUUID().toString();
            posSystem.setId(id);
        }
        posSystems.put(posSystem.getId(), posSystem);
    }

    @Override
    public PosSystem getPosSystemById(String id) {
        return posSystems.get(id);
    }

    @Override
    public void deletePosSystemById(String id) {
        posSystems.remove(id);
    }

    @Override
    public boolean posSystemExists(String id) {
        return posSystems.containsKey(id);
    }

    @Override
    public List<PosSystem> getAllPosSystems() {
        return posSystems.values().stream().toList();
    }

    @Override
    public List<PosSystem> getPosSystemsByStoreName(String storeName) {
        return posSystems.values().stream().filter(posSystem -> posSystem.getStoreName().equals(storeName)).toList();
    }

    @Override
    public PosSystem getPosSystemByStoreNameAndPosLane(String storeName, int posLane) {
        return posSystems.values().stream()
                .filter(posSystem -> posSystem.getStoreName().equals(storeName) && posSystem.getPosLane() == posLane)
                .findFirst().orElse(null);
    }

    @Override
    public boolean posSystemExistsByStoreNameAndPosLane(String storeName, int posLane) {
        return posSystems.values().stream()
                .anyMatch(posSystem -> posSystem.getStoreName().equals(storeName) && posSystem.getPosLane() == posLane);
    }
}
