package com.yehorychev.selenium;

import com.yehorychev.selenium.configs.ConfigProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getFlightBookingUrl());

            // Wait for the currency dropdown to be visible
            By currencyLocator = By.id("ctl00_mainContent_DropDownListCurrency");
            WebElement staticDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(currencyLocator));

            // Select USD by visible text
            Select dropdown = new Select(staticDropdown);
            dropdown.selectByVisibleText("USD");

            // Wait until the selected option text is "USD"
            wait.until(__ -> "USD".equals(dropdown.getFirstSelectedOption().getText()));

            // Verify page details
            String pageTitle = driver.getTitle();
            String pageUrl = Objects.requireNonNull(driver.getCurrentUrl()).split("\\?")[0];
            System.out.printf("The page title is: %s, and the page base URL is: %s", pageTitle, pageUrl);

            Assert.assertEquals(dropdown.getFirstSelectedOption().getText(), "USD");
        } finally {
            driver.quit();
        }
    }

    @Test
    void flightBookingPassengersDropdownTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getFlightBookingUrl());

            // Wait for and click the passengers info element to open the popup
            By paxInfoLocator = By.xpath("//div[@id='divpaxinfo']");
            WebElement paxInfo = wait.until(ExpectedConditions.elementToBeClickable(paxInfoLocator));
            paxInfo.click();

            By adultPlusBtnLocator = By.xpath("//span[@id='hrefIncAdt']");

            By childPlusBtnLocator = By.xpath("//span[@id='hrefIncChd']");

            By doneBtnLocator = By.xpath("//input[@id='btnclosepaxoption']");
            By passengersSelectedLocator = By.xpath("//div[@id='divpaxinfo']");

            // Click adult plus button just once to select 2 adults (1 adult is selected by default)
            WebElement adultPlusBtn = wait.until(ExpectedConditions.elementToBeClickable(adultPlusBtnLocator));
            adultPlusBtn.click();

            // Click child plus button twice to select 2 children
            WebElement childPlusBtn = wait.until(ExpectedConditions.elementToBeClickable(childPlusBtnLocator));
            childPlusBtn.click();
            childPlusBtn.click();

            // Click the Done button
            WebElement doneBtn = wait.until(ExpectedConditions.elementToBeClickable(doneBtnLocator));
            doneBtn.click();

            // Wait for the popup to close and verify the selected passengers
            WebElement passengersSelected = wait.until(ExpectedConditions.visibilityOfElementLocated(passengersSelectedLocator));
            String passengersText = passengersSelected.getText();

            // Assert that we have 2 adults and 2 children selected
            Assert.assertTrue(passengersText.contains("2 Adult") && passengersText.contains("2 Child"),
                    "Expected '2 Adult, 2 Child' but got: " + passengersText);
        } finally {
            driver.quit();
        }
    }
}
