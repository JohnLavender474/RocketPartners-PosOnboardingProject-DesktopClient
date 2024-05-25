package com.rocketpartners.onboarding.possystem.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a point of sale event. An event has a type and a map of properties.
 */
@ToString
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
     * @param type  The type of the event.
     * @param props The properties of the event.
     */
    public PosEvent(@NonNull PosEventType type, @NonNull Map<String, Object> props) {
        this.type = type;
        this.props = props;
    }

    /**
     * Returns true if the event contains a property with the specified key.
     *
     * @param key The key of the property.
     * @return True if the event contains the property, false otherwise.
     */
    public boolean containsProperty(@NonNull String key) {
        return props.containsKey(key);
    }

    /**
     * Add a property to the event.
     *
     * @param key The key of the property.
     * @return The event.
     */
    public Object getProperty(@NonNull String key) {
        return props.get(key);
    }

    /**
     * Get a property of the event and cast it to the specified class.
     *
     * @param key   The key of the property.
     * @param clazz The class to cast the property to.
     * @param <T>   The type of the property.
     * @return The property cast to the specified class.
     */
    public <T> T getProperty(@NonNull String key, @NonNull Class<T> clazz) {
        return clazz.cast(getProperty(key));
    }

    /**
     * Get a property of the event and cast it to the specified class. If the property does not exist, return the
     * default
     * value. If the property exists but cannot be cast to the specified class, an exception will be thrown.
     *
     * @param key          The key of the property.
     * @param defaultValue The default value to return if the property does not exist.
     * @return The property cast to the specified class or the default value if the property does not exist.
     */
    public Object getOrDefaultProperty(@NonNull String key, Object defaultValue) {
        return props.containsKey(key) ? getProperty(key) : defaultValue;
    }

    /**
     * Get a property of the event and cast it to the specified class. If the property does not exist, return the
     * default
     * value. If the property exists but cannot be cast to the specified class, an exception will be thrown.
     *
     * @param key          The key of the property.
     * @param clazz        The class to cast the property to.
     * @param defaultValue The default value to return if the property does not exist.
     * @param <T>          The type of the property.
     * @return The property cast to the specified class or the default value if the property does not exist.
     */
    public <T> T getOrDefaultProperty(@NonNull String key, @NonNull Class<T> clazz, T defaultValue) {
        return props.containsKey(key) ? getProperty(key, clazz) : defaultValue;
    }

    /**
     * Returns a copy of the properties map.
     *
     * @return A copy of the properties map.
     */
    public Map<String, Object> getCopyOfProps() {
        return new HashMap<>(props);
    }
}
