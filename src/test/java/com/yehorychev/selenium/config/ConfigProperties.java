package com.yehorychev.selenium.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class to manage test properties such as base URLs.
 * Reads properties from config.properties file.
 */
public class ConfigProperties {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (InputStream input = ConfigProperties.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new IOException("Configuration file not found: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration properties", e);
        }
    }

    /**
     * Get the base URL for Google
     *
     * @return Google base URL
     */
    public static String getGoogleUrl() {
        return getProperty("base.url.google");
    }

    /**
     * Get the base URL for Flight Booking
     *
     * @return Flight booking base URL
     */
    public static String getFlightBookingUrl() {
        return getProperty("base.url.flight.booking");
    }

    /**
     * Get the base URL for Practice Page
     *
     * @return Practice Page base URL
     */
    public static String getPracticePageUrl() {
        return getProperty("base.url.practice.page");
    }

    /**
     * Get the base URL for Green Kart
     *
     * @return Green Kart base URL
     */
    public static String getGreenKartUrl() {
        return getProperty("base.url.green.kart");
    }

    /**
     * Generic method to get any property value
     *
     * @param key the property key
     * @return the property value
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property not found: " + key);
        }
        return value;
    }
}

