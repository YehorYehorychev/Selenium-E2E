package com.yehorychev.selenium.helpers;

import com.yehorychev.selenium.config.ConfigProperties;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class WaitHelper {

    private static final Set<Class<? extends Throwable>> DEFAULT_IGNORED = Set.of(
            NoSuchElementException.class,
            StaleElementReferenceException.class
    );

    private final WebDriver driver;
    private final Duration defaultTimeout;
    private final Duration defaultPolling;
    private final Logger log = LoggerFactory.getLogger(WaitHelper.class);

    public WaitHelper(WebDriver driver, Duration explicitTimeout) {
        this(driver, explicitTimeout, ConfigProperties.getDefaultWaitPollingInterval());
    }

    public WaitHelper(WebDriver driver, Duration explicitTimeout, Duration pollingInterval) {
        this.driver = driver;
        this.defaultTimeout = explicitTimeout != null ? explicitTimeout : ConfigProperties.getDefaultWaitTimeout();
        this.defaultPolling = pollingInterval != null ? pollingInterval : ConfigProperties.getDefaultWaitPollingInterval();
    }

    public static WaitHelper fromConfig(WebDriver driver) {
        return new WaitHelper(driver, ConfigProperties.getDefaultWaitTimeout(), ConfigProperties.getDefaultWaitPollingInterval());
    }

    public LocatorWait forLocator(By locator) {
        return new LocatorWait(locator, defaultTimeout, defaultPolling);
    }

    public FluentWait<WebDriver> fluent(Duration timeout, Duration polling) {
        return new FluentWait<>(driver)
                .withTimeout(timeout != null ? timeout : defaultTimeout)
                .pollingEvery(polling != null ? polling : defaultPolling)
                .ignoreAll(DEFAULT_IGNORED);
    }

    public WebElement visibilityOf(By locator) {
        return forLocator(locator).visible().get();
    }

    public List<WebElement> visibilityOfAllElements(By locator) {
        return forLocator(locator).allVisible().getAll();
    }

    public List<WebElement> presenceOfAllElements(By locator) {
        return forLocator(locator).allPresent().getAll();
    }

    public WebElement elementToBeClickable(By locator) {
        return forLocator(locator).clickable().get();
    }

    public void textToBe(By locator, String expectedText) {
        until(ExpectedConditions.textToBe(locator, expectedText));
    }

    public List<WebElement> numberOfElementsToBe(By locator, int expectedCount) {
        return until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
    }

    public <T> T until(Function<WebDriver, T> condition) {
        try {
            return fluent(defaultTimeout, defaultPolling).until(condition);
        } catch (TimeoutException e) {
            throw new WaitTimeoutException("Wait condition timed out", e);
        }
    }

    public Alert alertIsPresent() {
        return until(ExpectedConditions.alertIsPresent());
    }

    public void waitForPageReady() {
        log.debug("Waiting for document.readyState=complete");
        until(driver -> {
            JavascriptExecutor executor = ensureJsExecutor(driver);
            return "complete".equals(String.valueOf(executor.executeScript("return document.readyState")));
        });
    }

    public void waitForAjaxComplete() {
        log.debug("Waiting for AJAX to finish");
        until(driver -> {
            JavascriptExecutor executor = ensureJsExecutor(driver);
            long jqueryActive = toLong(executor.executeScript("return window.jQuery ? jQuery.active : 0"));
            long fetchInflight = toLong(executor.executeScript("return window.___fetchInFlight || 0"));
            return jqueryActive + fetchInflight == 0;
        });
    }

    public void waitForAlertDismissed() {
        log.debug("Waiting for alert to be dismissed");
        until(driver -> {
            try {
                driver.switchTo().alert();
                return false;
            } catch (NoAlertPresentException ignored) {
                return true;
            }
        });
    }

    public void waitForElementToDisappear(By locator) {
        log.debug("Waiting for element to disappear: {}", locator);
        until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForStaleness(WebElement element) {
        log.debug("Waiting for staleness: {}", element);
        until(ExpectedConditions.stalenessOf(element));
    }

    public void waitForElementsGreaterThan(By locator, int count) {
        log.debug("Waiting for elements greater than {}: {}", count, locator);
        until(driver -> driver.findElements(locator).size() > count);
    }

    public WebElement waitForTextEquals(By locator, String expectedText) {
        log.debug("Waiting for text '{}' on {}", expectedText, locator);
        return until(driver -> {
            WebElement element = driver.findElement(locator);
            return expectedText.equals(element.getText().trim()) ? element : null;
        });
    }

    public void waitForNumberOfWindows(int expected) {
        log.debug("Waiting for at least {} windows", expected);
        until(driver -> driver.getWindowHandles().size() >= expected);
    }

    public void switchToNewChildWindow() {
        String currentHandle = driver.getWindowHandle();
        waitForNumberOfWindows(2);
        driver.getWindowHandles().stream()
                .filter(handle -> !handle.equals(currentHandle))
                .findFirst()
                .ifPresent(handle -> driver.switchTo().window(handle));
    }

    public void switchToParentWindow(String handle) {
        waitForWindowHandle(handle);
        driver.switchTo().window(handle);
    }

    public void waitForWindowHandle(String handle) {
        log.debug("Waiting for window handle: {}", handle);
        until(driver -> driver.getWindowHandles().contains(handle));
    }

    public void safeScrollIntoView(By locator) {
        log.debug("Scrolling into view: {}", locator);
        WebElement element = forLocator(locator).present().get();
        executeJs("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    public void retryingClick(By locator, int attempts, Duration backoff) {
        RuntimeException last = null;
        Duration effectiveBackoff = backoff != null ? backoff : defaultPolling;
        for (int i = 0; i < attempts; i++) {
            try {
                log.debug("Retrying click attempt {} for {}", i + 1, locator);
                elementToBeClickable(locator).click();
                return;
            } catch (RuntimeException e) {
                last = e;
                sleep(effectiveBackoff);
                safeScrollIntoView(locator);
            }
        }
        throw new WaitTimeoutException("Unable to click after attempts for locator: " + locator, last);
    }

    public JavascriptExecutor getJsExecutor() {
        return ensureJsExecutor(driver);
    }

    public Object executeJs(String script, Object... args) {
        return getJsExecutor().executeScript(script, args);
    }

    /**
     * Wait for any of the specified locators to become visible
     *
     * @param locators Variable number of By locators
     * @return First visible WebElement
     */
    public WebElement waitForAnyVisible(By... locators) {
        try {
            return new FluentWait<>(driver)
                    .withTimeout(defaultTimeout)
                    .pollingEvery(defaultPolling)
                    .ignoreAll(DEFAULT_IGNORED)
                    .until(d -> {
                        for (By locator : locators) {
                            try {
                                WebElement element = d.findElement(locator);
                                if (element.isDisplayed()) {
                                    return element;
                                }
                            } catch (Exception ignored) {
                                // Continue to next locator
                            }
                        }
                        return null;
                    });
        } catch (TimeoutException e) {
            throw new WaitTimeoutException(
                    "Timed out waiting for any of " + locators.length + " locators to be visible", e);
        }
    }

    private JavascriptExecutor ensureJsExecutor(WebDriver candidate) {
        if (!(candidate instanceof JavascriptExecutor executor)) {
            throw new IllegalStateException("Driver does not support JavaScript execution");
        }
        return executor;
    }

    private long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value == null) {
            return 0L;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    private void sleep(Duration backoff) {
        try {
            TimeUnit.MILLISECONDS.sleep(backoff.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public class LocatorWait {
        private final By locator;
        private Duration timeout;
        private Duration polling;

        private LocatorWait(By locator, Duration timeout, Duration polling) {
            this.locator = locator;
            this.timeout = timeout;
            this.polling = polling;
        }

        public LocatorWait withTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public LocatorWait withPolling(Duration polling) {
            this.polling = polling;
            return this;
        }

        public ElementResult visible() {
            return new ElementResult(wait(ExpectedConditions.visibilityOfElementLocated(locator)));
        }

        public ElementResult clickable() {
            return new ElementResult(wait(ExpectedConditions.elementToBeClickable(locator)));
        }

        public ElementResult present() {
            return new ElementResult(wait(ExpectedConditions.presenceOfElementLocated(locator)));
        }

        public ElementCollection allVisible() {
            return new ElementCollection(wait(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)));
        }

        public ElementCollection allPresent() {
            return new ElementCollection(wait(ExpectedConditions.presenceOfAllElementsLocatedBy(locator)));
        }

        private <T> T wait(ExpectedCondition<T> condition) {
            try {
                return new FluentWait<>(driver)
                        .withTimeout(timeout)
                        .pollingEvery(polling)
                        .ignoreAll(DEFAULT_IGNORED)
                        .until(condition);
            } catch (TimeoutException e) {
                throw new WaitTimeoutException("Timed out waiting for locator: " + locator, e);
            }
        }
    }

    public class ElementResult {
        private final WebElement element;

        private ElementResult(WebElement element) {
            this.element = element;
        }

        public WebElement get() {
            return element;
        }

        public String text() {
            return element.getText();
        }

        public void click() {
            element.click();
        }
    }

    public class ElementCollection {
        private final List<WebElement> elements;

        private ElementCollection(List<WebElement> elements) {
            this.elements = elements;
        }

        public List<WebElement> getAll() {
            return elements;
        }

        public int size() {
            return elements.size();
        }
    }
}
