package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import lombok.NonNull;
import lombok.ToString;

import java.util.*;

/**
 * An in-memory implementation of the {@link PosSystemRepository} interface.
 */
@ToString
public class InMemoryPosSystemRepository implements PosSystemRepository {

    private final Map<String, PosSystem> posSystems = new HashMap<>();

    @Override
    public void savePosSystem(@NonNull PosSystem posSystem) {
        if (posSystem.getId() == null || posSystem.getId().isBlank()) {
            String id = UUID.randomUUID().toString();
            posSystem.setId(id);
        }
        posSystems.put(posSystem.getId(), posSystem);
    }

    @Override
    public PosSystem getPosSystemById(@NonNull String id) {
        return posSystems.get(id);
    }

    @Override
    public void deletePosSystemById(@NonNull String id) {
        posSystems.remove(id);
    }

    @Override
    public boolean posSystemExists(@NonNull String id) {
        return posSystems.containsKey(id);
    }

    @Override
    public List<PosSystem> getAllPosSystems() {
        return new ArrayList<>(posSystems.values());
    }

    @Override
    public List<PosSystem> getPosSystemsByStoreName(@NonNull String storeName) {
        List<PosSystem> posSystems = new ArrayList<>();
        this.posSystems.values().forEach(posSystem -> {
            if (posSystem.getStoreName().equals(storeName)) {
                posSystems.add(posSystem);
            }
        });
        return posSystems;
    }

    @Override
    public PosSystem getPosSystemByStoreNameAndPosLane(@NonNull String storeName, int posLane) {
        return posSystems.values().stream()
                .filter(posSystem -> posSystem.getStoreName().equals(storeName) && posSystem.getPosLane() == posLane)
                .findFirst().orElse(null);
    }

    @Override
    public boolean posSystemExistsByStoreNameAndPosLane(@NonNull String storeName, int posLane) {
        return posSystems.values().stream()
                .anyMatch(posSystem -> posSystem.getStoreName().equals(storeName) && posSystem.getPosLane() == posLane);
    }
}
