package com.yehorychev.selenium;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.helpers.ScreenshotHelper;
import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public abstract class BaseTest {

    protected WebDriver driver;
    protected WaitHelper waitHelper;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        waitHelper = new WaitHelper(driver, Duration.ofSeconds(5));
        driver.navigate().to(ConfigProperties.getGreenKartUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver != null) {
            try {
                boolean failuresOnly = ConfigProperties.captureScreenshotsOnFailuresOnly();
                boolean shouldCapture = !failuresOnly || result.getStatus() == ITestResult.FAILURE;
                if (shouldCapture) {
                    ScreenshotHelper.capture(driver, result.getMethod().getMethodName());
                }
            } catch (RuntimeException screenshotError) {
                // screenshot failures should not block driver cleanup
            } finally {
                driver.quit();
            }
        }
    }
}
