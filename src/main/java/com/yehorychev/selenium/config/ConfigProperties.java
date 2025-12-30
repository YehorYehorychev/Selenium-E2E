package com.yehorychev.selenium.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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
     * Get the base URL for Shopping Login Page
     *
     * @return Shopping Login Page base URL
     */
    public static String getShoppingLoginPageUrl() {
        return getProperty("base.url.shopping");
    }

    public static String getShoppingApiBaseUrl() {
        URI uri = URI.create(getShoppingLoginPageUrl());
        return uri.getScheme() + "://" + uri.getHost();
    }

    public static String getShoppingUsername() {
        return getProperty("shopping.username");
    }

    public static String getShoppingPassword() {
        return getProperty("shopping.password");
    }

    /**
     * Get the base URL for Amazon
     *
     * @return Amazon base URL
     */
    public static String getAmazonUrl() {
        return getProperty("base.url.amazon");
    }

    /**
     * Get the directory where screenshots will be saved
     *
     * @return Screenshot directory path
     */
    public static Path getScreenshotDirectory() {
        return Path.of(getProperty("screenshot.directory"));
    }

    /**
     * Check if screenshots should be captured only on test failures
     *
     * @return true if screenshots should be captured on failures only, false otherwise
     */
    public static boolean captureScreenshotsOnFailuresOnly() {
        return Boolean.parseBoolean(getProperty("screenshot.failures.only"));
    }

    public static String getDefaultBrowser() {
        return properties.getProperty("browser.default", "chrome").trim();
    }

    public static String getBrowserOverride() {
        return System.getProperty("browser", System.getenv().getOrDefault("BROWSER", ""));
    }

    public static Duration getDefaultWaitTimeout() {
        return Duration.ofSeconds(Long.parseLong(getProperty("wait.default.seconds")));
    }

    public static Duration getDefaultWaitPollingInterval() {
        return Duration.ofMillis(Long.parseLong(getProperty("wait.polling.millis")));
    }

    public static boolean isHeadlessEnabled() {
        String override = System.getProperty("browser.headless",
                System.getenv().getOrDefault("BROWSER_HEADLESS", ""));
        if (override != null && !override.isBlank()) {
            return Boolean.parseBoolean(override.trim());
        }
        return Boolean.parseBoolean(properties.getProperty("browser.headless.enabled", "false"));
    }

    public static List<String> getHeadlessArguments() {
        String override = System.getProperty("browser.headless.args",
                System.getenv().getOrDefault("BROWSER_HEADLESS_ARGS", ""));
        String raw = (override != null && !override.isBlank())
                ? override
                : properties.getProperty("browser.headless.args", "--headless=new;--disable-gpu");
        return Arrays.stream(raw.split(";"))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .collect(Collectors.toList());
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

    public static String getShoppingCardNumber() {
        return getProperty("shopping.card.number");
    }

    public static String getShoppingCardCvv() {
        return getProperty("shopping.card.cvv");
    }

    public static boolean isFullPageScreenshotsEnabled() {
        return Boolean.parseBoolean(properties.getProperty("screenshot.fullpage.enabled", "true"));
    }

    public static long getFullPageScrollTimeoutMillis() {
        return Long.parseLong(properties.getProperty("screenshot.fullpage.scroll.timeout.millis", "100"));
    }

    public static double getFullPageDevicePixelRatio() {
        return Double.parseDouble(properties.getProperty("screenshot.fullpage.device.pixel.ratio", "1.0"));
    }

    /**
     * Get browser type from system property or config (default: chrome)
     */
    public static String getBrowser() {
        return System.getProperty("browser", properties.getProperty("browser", "chrome"));
    }

    /**
     * Check if headless mode is enabled
     */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("headless",
            properties.getProperty("headless", "false")));
    }

    /**
     * Get Selenium Grid URL if configured
     */
    public static String getSeleniumGridUrl() {
        return System.getProperty("selenium.grid.url",
            properties.getProperty("selenium.grid.url", ""));
    }
}
