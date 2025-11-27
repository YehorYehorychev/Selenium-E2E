package com.yehorychev.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Objects;

public class InitTest {

    @Test
    void googleTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        driver.get("https://www.google.com/");

        String pageTitle = driver.getTitle();
        String pageUrl = Objects.requireNonNull(driver.getCurrentUrl()).split("\\?")[0];

        System.out.printf("The page title is: %s, and the page base URL is: %s", pageTitle, pageUrl);

        driver.findElement(By.xpath("//textarea[@class='gLFyf']")).click();
        driver.findElement(By.xpath("//textarea[@class='gLFyf']")).sendKeys("Selenium");
        driver.findElement(By.xpath("//textarea[@class='gLFyf']")).sendKeys(Keys.ENTER);
        Assert.assertTrue(driver.findElement(By.xpath("(//span[contains(text(),'Selenium Test Automation')])[1]")).isDisplayed());

        driver.quit();
    }
}