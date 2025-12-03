package com.yehorychev.selenium;

import com.yehorychev.selenium.configs.ConfigProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getPracticePageUrl());

            By nameFieldLocator = By.xpath("//input[@id='name']");
            By confirmBtnLocator = By.xpath("//input[@id='confirmbtn']");

            WebElement nameField = wait.until(ExpectedConditions.elementToBeClickable(nameFieldLocator));
            nameField.sendKeys("Yehor");

            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmBtnLocator));
            confirmBtn.click();

            // Assert that alert is present
            wait.until(ExpectedConditions.alertIsPresent());
            String alertText = driver.switchTo().alert().getText();
            Assert.assertEquals(alertText, "Hello Yehor, Are you sure you want to confirm?");

            // Dismiss the alert
            driver.switchTo().alert().dismiss();

            // Verify alert is dismissed
            wait.until(driver1 -> {
                try {
                    driver1.switchTo().alert();
                    return false; // Alert is still present
                } catch (Exception e) {
                    return true; // Alert is dismissed
                }
            });
        } finally {
            driver.quit();
        }
    }
}
