package com.yehorychev.selenium.pages.common;

import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
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
        try {
            log.debug("Finding visible element: {}", locator);
            return waitHelper.visibilityOf(locator);
        } catch (TimeoutException e) {
            throw enrichAndWrap("Timed out waiting for visibility", locator, e);
        }
    }

    protected List<WebElement> findAll(By locator) {
        try {
            log.debug("Finding all visible elements: {}", locator);
            return waitHelper.visibilityOfAllElements(locator);
        } catch (TimeoutException e) {
            throw enrichAndWrap("Timed out waiting for elements", locator, e);
        }
    }

    protected void click(By locator) {
        retryingClick(locator, 3, Duration.ofMillis(200));
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

    protected void retryingClick(By locator, int attempts, Duration backoff) {
        RuntimeException lastError = null;
        for (int i = 0; i < attempts; i++) {
            try {
                log.debug("Click attempt {} for {}", i + 1, locator);
                WebElement clickable = waitHelper.elementToBeClickable(locator);
                clickable.click();
                return;
            } catch (RuntimeException e) {
                lastError = e;
                sleep(backoff);
                safeScrollIntoView(locator);
            }
        }
        throw enrichAndWrap("Unable to click element after retries", locator, lastError);
    }

    protected void safeScrollIntoView(By locator) {
        try {
            log.debug("Scrolling element into view: {}", locator);
            jsExecutor().executeScript("arguments[0].scrollIntoView({block: 'center'});", driver.findElement(locator));
        } catch (RuntimeException e) {
            log.warn("Failed to scroll into view {}", locator, e);
        }
    }

    protected void waitForPageReady() {
        log.debug("Waiting for document.readyState=complete");
        waitHelper.until((ExpectedCondition<Boolean>) drv ->
                "complete".equals(jsExecutor().executeScript("return document.readyState")));
    }

    protected void waitForAjaxComplete() {
        log.debug("Waiting for AJAX queue to finish");
        waitHelper.until(driver -> {
            JavascriptExecutor js = jsExecutor();
            Object jqueryActive = js.executeScript("return window.jQuery ? jQuery.active : 0");
            Object fetchInflight = js.executeScript("return window.___fetchInFlight || 0");
            long active = toLong(jqueryActive) + toLong(fetchInflight);
            return active == 0;
        });
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

    private long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    private void sleep(Duration backoff) {
        try {
            Thread.sleep(backoff.toMillis());
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private RuntimeException enrichAndWrap(String message, By locator, Throwable cause) {
        String formatted = String.format("%s - locator: %s", message, locator);
        log.error(formatted, cause);
        return new RuntimeException(formatted, cause);
    }
}
