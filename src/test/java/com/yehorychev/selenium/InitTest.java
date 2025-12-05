package com.yehorychev.selenium;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Objects;

public class InitTest {

    @Test
    void googleTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        WaitHelper waitHelper = new WaitHelper(driver, Duration.ofSeconds(5));
        driver.navigate().to(ConfigProperties.getGoogleUrl());

        String pageTitle = driver.getTitle();
        String pageUrl = Objects.requireNonNull(driver.getCurrentUrl()).split("\\?")[0];

        System.out.printf("The page title is: %s, and the page base URL is: %s", pageTitle, pageUrl);

        WebElement searchField = driver.findElement(By.xpath("//textarea[@class='gLFyf']"));
        searchField.click();
        searchField.sendKeys("Selenium");
        searchField.sendKeys(Keys.ENTER);

        WebElement searchOutput = waitHelper.visibilityOf(
                By.xpath("(//cite[@role='text'])[1]")
        );

        Assert.assertNotNull(searchOutput);
        Assert.assertTrue(searchOutput.isDisplayed());
        System.out.println(System.lineSeparator() + searchOutput.getText());

        driver.quit();
    }
}