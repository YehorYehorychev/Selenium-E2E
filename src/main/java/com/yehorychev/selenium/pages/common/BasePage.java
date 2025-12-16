package com.yehorychev.selenium.pages.common;

import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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

    protected WebElement findMatching(By locator, Predicate<WebElement> predicate, String description) {
        return findAll(locator).stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Unable to find element by " + description + " within " + locator));
    }

    protected WebElement findByText(By locator, String expectedText) {
        log.debug("Searching for text '{}' within {}", expectedText, locator);
        return findMatching(locator, element -> expectedText.equalsIgnoreCase(element.getText().trim()), "text '" + expectedText + "'");
    }

    protected List<String> getTexts(By locator) {
        return findAll(locator).stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    protected void click(By locator) {
        log.debug("Clicking element: {}", locator);
        waitHelper.retryingClick(locator, DEFAULT_CLICK_RETRIES, DEFAULT_CLICK_BACKOFF);
    }

    protected void safeClick(By locator) {
        log.debug("Safely clicking element: {}", locator);
        waitHelper.safeScrollIntoView(locator);
        click(locator);
    }

    protected void jsClick(By locator) {
        log.debug("Clicking via JS: {}", locator);
        WebElement element = find(locator);
        waitHelper.executeJs("arguments[0].click();", element);
    }

    protected void hover(By locator) {
        log.debug("Hovering over element: {}", locator);
        actions().moveToElement(find(locator)).perform();
    }

    protected void dragAndDrop(By source, By target) {
        log.debug("Dragging {} to {}", source, target);
        actions().dragAndDrop(find(source), find(target)).perform();
    }

    protected void selectByVisibleText(By locator, String visibleText) {
        log.debug("Selecting '{}' in {}", visibleText, locator);
        new Select(find(locator)).selectByVisibleText(visibleText);
    }

    protected void selectByValue(By locator, String value) {
        log.debug("Selecting value '{}' in {}", value, locator);
        new Select(find(locator)).selectByValue(value);
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

    protected String getAttribute(By locator, String attribute) {
        String value = find(locator).getAttribute(attribute);
        log.debug("Attribute '{}' for {} is '{}'", attribute, locator, value);
        return value;
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

    protected void waitForElementToDisappear(By locator) {
        log.debug("Waiting for element to disappear: {}", locator);
        waitHelper.waitForElementToDisappear(locator);
    }

    protected void waitForTextEquals(By locator, String expectedText) {
        log.debug("Waiting for text '{}' on {}", expectedText, locator);
        waitHelper.waitForTextEquals(locator, expectedText);
    }

    protected void waitForElementsGreaterThan(By locator, int count) {
        log.debug("Waiting for more than {} elements located by {}", count, locator);
        waitHelper.waitForElementsGreaterThan(locator, count);
    }

    protected void waitForNumberOfWindows(int expected) {
        waitHelper.waitForNumberOfWindows(expected);
    }

    protected void waitForWindowHandle(String handle) {
        waitHelper.waitForWindowHandle(handle);
    }

    protected void waitForPageReady() {
        waitHelper.waitForPageReady();
    }

    protected void waitForAjaxComplete() {
        waitHelper.waitForAjaxComplete();
    }

    protected void waitForPageReadyAndAjax() {
        waitForPageReady();
        waitForAjaxComplete();
    }

    protected void assertTextEquals(By locator, String expected) {
        String actual = getText(locator);
        log.debug("Asserting text equals. Locator: {}, expected: {}, actual: {}", locator, expected, actual);
        Assert.assertEquals(actual.trim(), expected.trim(), "Text mismatch for locator " + locator);
    }

    protected void assertTextContains(By locator, String expectedFragment) {
        String actual = getText(locator).trim();
        log.debug("Asserting '{}' contains '{}': {}", locator, expectedFragment, actual);
        Assert.assertTrue(actual.contains(expectedFragment), "Expected text to contain '" + expectedFragment + "' but was '" + actual + "'");
    }

    protected void assertAttributeEquals(By locator, String attribute, String expected) {
        String actual = getAttribute(locator, attribute);
        Assert.assertEquals(actual, expected, "Attribute mismatch for " + locator + " attribute " + attribute);
    }

    protected void assertElementCount(By locator, int expected) {
        List<WebElement> elements = waitHelper.numberOfElementsToBe(locator, expected);
        Assert.assertEquals(elements.size(), expected, "Unexpected number of elements for " + locator);
    }

    protected void assertCurrentUrlContains(String fragment) {
        String currentUrl = driver.getCurrentUrl();
        log.debug("Validating URL contains fragment '{}': {}", fragment, currentUrl);
        Assert.assertTrue(currentUrl.contains(fragment), "Current URL does not contain fragment: " + fragment);
    }

    protected void assertCurrentUrlEquals(String expectedUrl) {
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, expectedUrl, "Unexpected current URL");
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

    protected void setLocalStorageItem(String key, String value) {
        log.debug("Setting localStorage item {}", key);
        jsExecutor().executeScript("window.localStorage.setItem(arguments[0], arguments[1]);", key, value);
    }

    protected void clearLocalStorageItem(String key) {
        log.debug("Removing localStorage item {}", key);
        jsExecutor().executeScript("window.localStorage.removeItem(arguments[0]);", key);
    }

    protected void clearLocalStorage() {
        log.debug("Clearing localStorage");
        jsExecutor().executeScript("window.localStorage.clear();");
    }
}
