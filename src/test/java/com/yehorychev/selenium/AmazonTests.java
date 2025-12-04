package com.yehorychev.selenium;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.waits.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

import java.time.Duration;

public class AmazonTests {

    @Test
    void amazonActionsTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WaitHelper waitHelper = new WaitHelper(driver, Duration.ofSeconds(5));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getAmazonUrl());
            Actions actions = new Actions(driver);

            By accountsAndListsLocator = By.xpath("//div[@id='nav-link-accountList']");
            WebElement accountsAndLists = waitHelper.visibilityOf(accountsAndListsLocator);

            actions.moveToElement(accountsAndLists).build().perform();
        } finally {
            driver.quit();
        }
    }
}
