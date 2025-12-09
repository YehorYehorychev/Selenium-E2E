package com.yehorychev.selenium;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlertsTest {

    @Test
    void dismissAlertTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WaitHelper waitHelper = new WaitHelper(driver, Duration.ofSeconds(5));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getPracticePageUrl());

            By nameFieldLocator = By.xpath("//input[@id='name']");
            By confirmBtnLocator = By.xpath("//input[@id='confirmbtn']");

            WebElement nameField = waitHelper.elementToBeClickable(nameFieldLocator);
            nameField.sendKeys("Yehor");

            WebElement confirmBtn = waitHelper.elementToBeClickable(confirmBtnLocator);
            confirmBtn.click();

            // Assert that alert is present
            String alertText = waitHelper.alertIsPresent().getText();
            Assert.assertEquals(alertText, "Hello Yehor, Are you sure you want to confirm?");

            // Dismiss the alert
            driver.switchTo().alert().dismiss();

            // Verify alert is dismissed
            waitHelper.until(d -> {
                try {
                    d.switchTo().alert();
                    return false; // Alert is still present
                } catch (Exception e) {
                    return true; // Alert is dismissed
                }
            });
        } finally {
            driver.quit();
        }
    }

    @Test
    void scrollPageTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WaitHelper waitHelper = new WaitHelper(driver, Duration.ofSeconds(5));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getPracticePageUrl());

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0,500);");

            By webTableLocator = By.xpath("//fieldset[2]");
            WebElement webTableFieldset = waitHelper.visibilityOf(webTableLocator);
            // ensure the scrollable table area is scrolled so all rows/cells are available
            js.executeScript("document.querySelector('.tableFixHead').scrollTop=5000");

            // Wait for rows to be present and then retrieve the 4th row's cell values
            By rowsLocator = By.cssSelector(".tableFixHead tr");
            List<WebElement> rows = waitHelper.presenceOfAllElements(rowsLocator);

            List<String> values = new ArrayList<>();

            if (rows.size() >= 4) {
                WebElement fourthRow = rows.get(3); // zero-based index -> 3 is the 4th row
                List<WebElement> cells = fourthRow.findElements(By.tagName("td"));

                if (!cells.isEmpty()) {
                    values = cells.stream()
                            .map(WebElement::getText)
                            .collect(Collectors.toList());

                    System.out.printf("Values in 4th row (cells count %d): %s%n", values.size(), values);
                }
            }

            // If the 4th row didn't yield expected values, fall back to reading the 4th column across all rows
            if (values.size() != 9) {
                List<WebElement> colValues = waitHelper.presenceOfAllElements(By.cssSelector(".tableFixHead td:nth-child(4)"));
                if (!colValues.isEmpty()) {
                    values = colValues.stream()
                            .map(WebElement::getText)
                            .collect(Collectors.toList());

                    System.out.printf("Values in 4th column (found %d): %s%n", values.size(), values);
                } else {
                    System.out.printf("Could not find values in 4th row or 4th column; rows=%d, rowCells=%d%n", rows.size(), values.size());
                }
            }
            Assert.assertTrue(webTableFieldset.isDisplayed());
        } finally {
            driver.quit();
        }
    }

    @Test
    void brokenLinksTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WaitHelper waitHelper = new WaitHelper(driver, Duration.ofSeconds(5));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getPracticePageUrl());

        } finally {
            driver.quit();
        }
    }
}
