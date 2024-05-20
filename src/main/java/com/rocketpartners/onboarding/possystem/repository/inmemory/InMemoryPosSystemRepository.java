package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InMemoryPosSystemRepository implements PosSystemRepository {

    @Override
    public void savePosSystem(PosSystem posSystem) {

    }

    @Override
    public PosSystem getPosSystemById(String id) {
        return null;
    }

    @Override
    public void deletePosSystemById(String id) {

    }

    @Override
    public boolean posSystemExists(String id) {
        return false;
    }

    @Override
    public List<PosSystem> getAllPosSystems() {
        return List.of();
    }

    @Override
    public List<PosSystem> getPosSystemsByStoreName(String storeName) {
        return List.of();
    }

    @Override
    public PosSystem getPosSystemByStoreNameAndPosLane(String storeName, int posLane) {
        return null;
    }

    @Override
    public boolean posSystemExistsByStoreNameAndPosLane(String storeName, int posLane) {
        return false;
    }
}
