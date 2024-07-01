package com.rocketpartners.onboarding.possystem.component;

/**
 * Interface for all systems in the POS system.
 */
public interface IComponent {

    /**
     * Boot up the component.
     */
    default void bootUp() {
    }

    /**
     * Update the component.
     */
    default void update() {
    }

    /**
     * Shut down the component.
     */
    default void shutDown() {
    }
}
