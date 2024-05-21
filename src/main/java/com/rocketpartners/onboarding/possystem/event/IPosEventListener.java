package com.rocketpartners.onboarding.possystem.event;

import lombok.NonNull;

import java.util.Set;

public interface IPosEventListener {

    /**
     * Get the event types that this listener is interested in. All events with an event type not contained in the
     * returned set will not be received.
     *
     * @return The event types that this listener is interested in.
     */
    @NonNull Set<PosEventType> getEventTypesToListenFor();

    /**
     * Called when a POS event is dispatched.
     *
     * @param event The event that was dispatched.
     */
    void onPosEvent(@NonNull PosEvent event);
}
