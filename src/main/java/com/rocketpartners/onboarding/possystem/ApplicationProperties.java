package com.rocketpartners.onboarding.possystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton class to load and provide access to application properties.
 */
public class ApplicationProperties {

    private static final String PROPS_PATH = "src/main/resources/application.properties";

    private final Properties properties;

    public ApplicationProperties() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(PROPS_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application properties", e);
        }
    }

    /**
     * Get the value of a property.
     *
     * @param key the property key
     * @return the property value
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
