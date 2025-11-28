package com.yehorychev.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Objects;

public class FlightBookingTest {

    @Test
    void flightBookingTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.navigate().to(ConfigProperties.getFlightBookingUrl());

        String pageTitle = driver.getTitle();
        String pageUrl = Objects.requireNonNull(driver.getCurrentUrl()).split("\\?")[0];

        driver.quit();
    }
}
