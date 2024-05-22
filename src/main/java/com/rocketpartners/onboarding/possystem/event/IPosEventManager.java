package com.rocketpartners.onboarding.possystem.event;

/**
 * Interface for the POS event manager. The POS event manager is responsible for managing POS event listeners
 * and dispatching events to those listeners. This should be used at the top level of the POS system to manage
 * POS events.
 */
public interface IPosEventManager extends IPosEventDispatcher {

    /**
     * Register a POS event listener.
     *
     * @param listener The POS event listener to register.
     */
    void registerPosEventListener(IPosEventListener listener);

    /**
     * Unregister a POS event listener.
     *
     * @param listener The POS event listener to unregister.
     */
    void unregisterPosEventListener(IPosEventListener listener);
}
