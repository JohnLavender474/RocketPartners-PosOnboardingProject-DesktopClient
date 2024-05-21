package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.Application;

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
        if (Application.DEBUG) {
            System.out.println("[BackOfficeComponent] Creating back office component");
        }
        posComponents = new ArrayList<>();
    }

    /**
     * Add a POS component to the back office.
     *
     * @param posController the POS component to add
     */
    public void addPosComponent(PosComponent posController) {
        posComponents.add(posController);
        if (Application.DEBUG) {
            System.out.println("[BackOfficeComponent] Added POS component to back office: " + posController);
        }
    }

    @Override
    public void bootUp() {
        if (Application.DEBUG) {
            System.out.println("[BackOfficeComponent] Booting up back office component");
        }
        posComponents.forEach(PosComponent::bootUp);
    }

    @Override
    public void update() {
        posComponents.forEach(PosComponent::update);
    }

    @Override
    public void shutdown() {
        if (Application.DEBUG) {
            System.out.println("[BackOfficeComponent] Shutting down back office component");
        }
        posComponents.forEach(PosComponent::shutdown);
    }
}
