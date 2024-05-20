package com.rocketpartners.onboarding.possystem.component;

/**
 * Interface for all systems in the POS system.
 */
public interface IComponent {

    /**
     * Boot up the system.
     */
    default void bootUp() {
    }

    /**
     * Update the system.
     */
    default void update() {
    }

    /**
     * Shutdown the system.
     */
    default void shutdown() {
    }
}
