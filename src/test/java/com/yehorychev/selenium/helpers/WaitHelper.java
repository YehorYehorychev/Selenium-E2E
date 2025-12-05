package com.yehorychev.selenium.helpers;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class WaitHelper {

    private final WebDriver driver;
    private final Duration timeout;

    public WaitHelper(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.timeout = timeout;
    }

    private WebDriverWait buildWait() {
        return new WebDriverWait(driver, timeout);
    }

    public WebElement visibilityOf(By locator) {
        return buildWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement elementToBeClickable(By locator) {
        return buildWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void textToBe(By locator, String expectedText) {
        buildWait().until(ExpectedConditions.textToBe(locator, expectedText));
    }

    public List<WebElement> numberOfElementsToBe(By locator, int expectedCount) {
        return buildWait().until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
    }

    public List<WebElement> presenceOfAllElements(By locator) {
        return buildWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public List<WebElement> visibilityOfAllElements(By locator) {
        return buildWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public <T> T until(Function<WebDriver, T> condition) {
        return buildWait().until(condition);
    }

    public Alert alertIsPresent() {
        return buildWait().until(ExpectedConditions.alertIsPresent());
    }

    public WebElement waitForCartCount(By locator, int expectedCount) {
        // retries until the cart badge shows the targeted count
        return buildWait().until(driver -> {
            WebElement element = driver.findElement(locator);
            return String.valueOf(expectedCount).equals(element.getText().trim()) ? element : null;
        });
    }
}
