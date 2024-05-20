package com.rocketpartners.onboarding.possystem.controller;

/**
 * Interface for all systems in the POS system.
 */
public interface IController {

    /**
     * Boot up the system.
     */
    void bootUp();

    /**
     * Update the system.
     */
    void update();

    /**
     * Shutdown the system.
     */
    void shutdown();
}
