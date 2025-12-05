package com.yehorychev.selenium;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.waits.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

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

            Assert.assertTrue(webTableFieldset.isDisplayed());
        } finally {
            driver.quit();
        }
    }
}
