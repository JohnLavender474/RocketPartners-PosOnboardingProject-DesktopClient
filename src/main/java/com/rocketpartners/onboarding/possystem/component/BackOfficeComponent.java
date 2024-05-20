package com.rocketpartners.onboarding.possystem.component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * The back office component of the POS system. This component is responsible for managing all POS components.
 */
@Component
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

    /**
     * {@inheritDoc}
     * <p>
     * Called when the application is starting up via the {@link PostConstruct} annotation.
     */
    @PostConstruct
    @Override
    public void bootUp() {
        posComponents.forEach(PosComponent::bootUp);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Called at a fixed rate of 500 milliseconds via the {@link Scheduled} annotation.
     */
    @Scheduled(fixedRate = 500)
    @Override
    public void update() {
        posComponents.forEach(PosComponent::update);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Called when the application is shutting down via the {@link PreDestroy} annotation.
     */
    @PreDestroy
    @Override
    public void shutdown() {
        posComponents.forEach(PosComponent::shutdown);
    }
}
