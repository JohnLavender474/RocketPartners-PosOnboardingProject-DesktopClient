package com.rocketpartners.onboarding.possystem.event;

import java.util.Set;

public interface IPosEventListener {

    /**
     * Get the event types that this listener is interested in. All events with an event type not contained in the
     * returned set will not be received. If the returned set is empty, the listener will be notified of all events.
     *
     * @return The event types that this listener is interested in.
     */
    Set<PosEventType> getEventTypesToListenFor();

    /**
     * Called when a POS event is dispatched.
     *
     * @param event The event that was dispatched.
     */
    void onPosEvent(PosEvent event);
}
