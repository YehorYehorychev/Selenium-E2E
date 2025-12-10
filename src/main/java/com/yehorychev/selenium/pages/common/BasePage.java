package com.yehorychev.selenium.pages.common;

import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitHelper waitHelper;

    protected BasePage(WebDriver driver, WaitHelper waitHelper) {
        this.driver = driver;
        this.waitHelper = waitHelper;
    }

    public String getTitle() {
        return driver.getTitle();
    }

    protected Actions actions() {
        return new Actions(driver);
    }

    protected JavascriptExecutor jsExecutor() {
        if (!(driver instanceof JavascriptExecutor executor)) {
            throw new IllegalStateException("Driver does not support JavaScript execution");
        }
        return executor;
    }

    protected Keys platformControlKey() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        return osName.contains("mac") ? Keys.COMMAND : Keys.CONTROL;
    }
}
