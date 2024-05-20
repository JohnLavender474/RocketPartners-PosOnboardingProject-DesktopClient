package com.rocketpartners.onboarding.possystem.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a point of sale event. An event has a type and a map of properties.
 *
 * @param type  The type of the event.
 * @param props The properties of the event.
 */
public record PosEvent(String type, Map<String, Object> props) {

    /**
     * Constructor that accepts a type and creates an empty properties map.
     *
     * @param type The type of the event.
     */
    public PosEvent(String type) {
        this(type, new HashMap<>());
    }
}
