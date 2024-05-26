package com.rocketpartners.onboarding.possystem.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A simple key-value pair.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pair<K, V> {

    private K key;
    private V value;

    /**
     * Creates a new pair.
     *
     * @param key   the key
     * @param value the value
     * @param <K>   the key type
     * @param <V>   the value type
     * @return the pair
     */
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}
