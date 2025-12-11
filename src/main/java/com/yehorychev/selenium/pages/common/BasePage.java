package com.yehorychev.selenium.pages.common;

import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitHelper waitHelper;
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private static final int DEFAULT_CLICK_RETRIES = 3;
    private static final Duration DEFAULT_CLICK_BACKOFF = Duration.ofMillis(200);

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

    protected WebElement find(By locator) {
        log.debug("Finding visible element: {}", locator);
        return waitHelper.visibilityOf(locator);
    }

    protected List<WebElement> findAll(By locator) {
        log.debug("Finding all visible elements: {}", locator);
        return waitHelper.visibilityOfAllElements(locator);
    }

    protected void click(By locator) {
        log.debug("Clicking element: {}", locator);
        waitHelper.retryingClick(locator, DEFAULT_CLICK_RETRIES, DEFAULT_CLICK_BACKOFF);
    }

    protected void type(By locator, String text) {
        WebElement element = find(locator);
        log.debug("Typing into element {} text: {}", locator, text);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        String text = find(locator).getText();
        log.debug("Text for {} is '{}'", locator, text);
        return text;
    }

    protected boolean isVisible(By locator) {
        try {
            find(locator);
            return true;
        } catch (RuntimeException e) {
            log.debug("Element not visible {}", locator, e);
            return false;
        }
    }

    protected void safeScrollIntoView(By locator) {
        log.debug("Scrolling element into view: {}", locator);
        waitHelper.safeScrollIntoView(locator);
    }

    protected void waitForPageReady() {
        waitHelper.waitForPageReady();
    }

    protected void waitForAjaxComplete() {
        waitHelper.waitForAjaxComplete();
    }

    protected void assertTextEquals(By locator, String expected) {
        String actual = getText(locator);
        log.debug("Asserting text equals. Locator: {}, expected: {}, actual: {}", locator, expected, actual);
        Assert.assertEquals(actual.trim(), expected.trim(), "Text mismatch for locator " + locator);
    }

    protected void assertCurrentUrlContains(String fragment) {
        String currentUrl = driver.getCurrentUrl();
        log.debug("Validating URL contains fragment '{}': {}", fragment, currentUrl);
        Assert.assertTrue(currentUrl.contains(fragment), "Current URL does not contain fragment: " + fragment);
    }

    protected void verifyElementPresent(By locator) {
        if (!isVisible(locator)) {
            throw new AssertionError("Expected element to be present: " + locator);
        }
    }

    protected void switchToWindow(String titleOrHandle) {
        log.debug("Switching to window: {}", titleOrHandle);
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (handle.equals(titleOrHandle) || driver.getTitle().equals(titleOrHandle)) {
                return;
            }
        }
        throw new IllegalArgumentException("Window not found: " + titleOrHandle);
    }

    protected void switchToFrame(By locator) {
        WebElement frame = find(locator);
        log.debug("Switching to frame located by {}", locator);
        driver.switchTo().frame(frame);
    }

    protected void openLinkInNewTab(By locator) {
        WebElement link = find(locator);
        log.debug("Opening link in new tab: {}", locator);
        actions().keyDown(platformControlKey()).click(link).keyUp(platformControlKey()).perform();
    }

    protected void addCookie(String name, String value) {
        log.debug("Adding cookie {}", name);
        driver.manage().addCookie(new org.openqa.selenium.Cookie(name, value));
    }

    protected void deleteCookie(String name) {
        log.debug("Deleting cookie {}", name);
        driver.manage().deleteCookieNamed(name);
    }

    protected void clearCookies() {
        log.debug("Clearing all cookies");
        driver.manage().deleteAllCookies();
    }
}
