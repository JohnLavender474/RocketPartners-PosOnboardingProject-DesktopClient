package com.rocketpartners.onboarding.possystem.component;

import java.util.ArrayList;
import java.util.List;

public class BackOfficeComponent implements IComponent {

    private final List<PosComponent> posControllers;

    public BackOfficeComponent() {
        posControllers = new ArrayList<>();
    }

    public void addChildController(PosComponent posController) {
        posControllers.add(posController);
    }

    @Override
    public void bootUp() {
        posControllers.forEach(PosComponent::bootUp);
    }

    @Override
    public void update() {
        posControllers.forEach(PosComponent::update);
    }

    @Override
    public void shutdown() {
        posControllers.forEach(PosComponent::shutdown);
    }
}
