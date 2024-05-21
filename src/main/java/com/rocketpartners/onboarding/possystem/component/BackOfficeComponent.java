package com.rocketpartners.onboarding.possystem.component;

import java.util.ArrayList;
import java.util.List;

/**
 * The back office component of the POS system. This component is responsible for managing all POS components.
 */
public class BackOfficeComponent implements IComponent {

    private final List<PosComponent> posComponents;

    /**
     * Constructor that initializes the list of POS components.
     */
    public BackOfficeComponent() {
        posComponents = new ArrayList<>();
    }

    /**
     * Add a POS component to the back office.
     *
     * @param posController the POS component to add
     */
    public void addPosComponent(PosComponent posController) {
        posComponents.add(posController);
    }

    @Override
    public void bootUp() {
        posComponents.forEach(PosComponent::bootUp);
    }

    @Override
    public void update() {
        posComponents.forEach(PosComponent::update);
    }

    @Override
    public void shutdown() {
        posComponents.forEach(PosComponent::shutdown);
    }
}
