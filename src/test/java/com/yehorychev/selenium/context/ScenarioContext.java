package com.yehorychev.selenium.context;

import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * Scenario Context to share state between Cucumber steps within a single scenario.
 * Uses PicoContainer for dependency injection - one instance per scenario.
 */
public class ScenarioContext {

    private WebDriver driver;
    private WaitHelper waitHelper;
    private final Map<String, Object> context = new HashMap<>();

    /**
     * Get WebDriver instance for this scenario
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Set WebDriver instance for this scenario
     */
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Get WaitHelper instance for this scenario
     */
    public WaitHelper getWaitHelper() {
        return waitHelper;
    }

    /**
     * Set WaitHelper instance for this scenario
     */
    public void setWaitHelper(WaitHelper waitHelper) {
        this.waitHelper = waitHelper;
    }

    /**
     * Store a value in scenario context
     */
    public void set(String key, Object value) {
        context.put(key, value);
    }

    /**
     * Retrieve a value from scenario context
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    /**
     * Retrieve a value with default if not present
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return context.containsKey(key) ? (T) context.get(key) : defaultValue;
    }

    /**
     * Check if key exists in context
     */
    public boolean contains(String key) {
        return context.containsKey(key);
    }

    /**
     * Clear all context data
     */
    public void clear() {
        context.clear();
    }

    /**
     * Get scenario name (if set by hooks)
     */
    public String getScenarioName() {
        return get("scenarioName", "Unknown Scenario");
    }

    /**
     * Set scenario name (called by hooks)
     */
    public void setScenarioName(String scenarioName) {
        set("scenarioName", scenarioName);
    }
}

