package com.rocketpartners.onboarding.possystem.event;

import lombok.NonNull;

/**
 * Interface for classes that can dispatch POS events. This interface is not intended for the POS event manager, but
 * rather for classes that can dispatch POS events up the chain.
 */
public interface IPosEventDispatcher {

    /**
     * Dispatch a POS event.
     *
     * @param event The POS event to dispatch.
     */
    void dispatchPosEvent(@NonNull PosEvent event);
}
