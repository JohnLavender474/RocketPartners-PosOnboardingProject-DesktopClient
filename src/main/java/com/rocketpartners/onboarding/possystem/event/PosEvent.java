package com.rocketpartners.onboarding.possystem.event;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a point of sale event. An event has a type and a map of properties.
 */
public class PosEvent {

    @Getter
    private final PosEventType type;
    private final Map<String, Object> props;

    /**
     * Constructor that accepts a type and creates an empty properties map.
     *
     * @param type The type of the event.
     */
    public PosEvent(@NonNull PosEventType type) {
        this(type, new HashMap<>());
    }

    /**
     * Constructor that accepts a type and a map of properties.
     *
     * @param type The type of the event.
     * @param props The properties of the event.
     */
    public PosEvent(@NonNull PosEventType type, @NonNull Map<String, Object> props) {
        this.type = type;
        this.props = props;
    }

    /**
     * Add a property to the event.
     *
     * @param key The key of the property.
     * @return The event.
     */
    public Object getProperty(String key) {
        return props.get(key);
    }

    /**
     * Get a property of the event and cast it to the specified class.
     *
     * @param key The key of the property.
     * @param clazz The class to cast the property to.
     * @return The property cast to the specified class.
     * @param <T> The type of the property.
     */
    public <T> T getProperty(String key, Class<T> clazz) {
        return clazz.cast(getProperty(key));
    }
}
