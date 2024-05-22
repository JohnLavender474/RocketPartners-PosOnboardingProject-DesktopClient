package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The back office component of the POS system. This component is responsible for managing all POS components.
 */
public class BackOfficeComponent implements IComponent {

    @NonNull
    private final ItemBookLoaderComponent itemBookLoaderComponent;
    @NonNull
    private final ItemService itemService;
    @NonNull
    private final List<PosComponent> posComponents;

    /**
     * Constructor that initializes the list of POS components.
     */
    public BackOfficeComponent(@NonNull ItemBookLoaderComponent itemBookLoaderComponent,
                               @NonNull ItemService itemService) {
        if (Application.DEBUG) {
            System.out.println("[BackOfficeComponent] Creating back office component");
        }
        this.itemBookLoaderComponent = itemBookLoaderComponent;
        this.itemService = itemService;
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
        itemBookLoaderComponent.loadItemBook(itemService);
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
