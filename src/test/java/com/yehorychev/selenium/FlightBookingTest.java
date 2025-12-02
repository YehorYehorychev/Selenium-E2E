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
            By paxInfoLocator = By.id("divpaxinfo");
            WebElement paxInfo = wait.until(ExpectedConditions.elementToBeClickable(paxInfoLocator));
            paxInfo.click();

            // Wait for and select 3 adults
            By adultLocator = By.id("ctl00_mainContent_ddl_Adult");
            WebElement adultSelectEl = wait.until(ExpectedConditions.visibilityOfElementLocated(adultLocator));
            Select adultSelect = new Select(adultSelectEl);
            adultSelect.selectByValue("3");

            // Wait for and select 2 children
            By childLocator = By.id("ctl00_mainContent_ddl_Child");
            WebElement childSelectEl = wait.until(ExpectedConditions.visibilityOfElementLocated(childLocator));
            Select childSelect = new Select(childSelectEl);
            childSelect.selectByValue("2");

            // Try clicking a Done button to close the popup
            boolean doneClicked = false;
            By[] doneLocators = new By[]{
                    By.id("btnclosepaxoption"),
                    By.xpath("//input[@value='Done']"),
                    By.xpath("//button[text()='Done']"),
                    By.xpath("//*[text()='Done']")
            };

            for (By loc : doneLocators) {
                java.util.List<WebElement> elems = driver.findElements(loc);
                if (!elems.isEmpty()) {
                    for (WebElement el : elems) {
                        try {
                            if (el.isDisplayed()) {
                                el.click();
                                doneClicked = true;
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    if (doneClicked) break;
                }
            }

            if (!doneClicked) {
                // Fallback: click the summary to close popup
                paxInfo.click();
            }

            // Wait for and verify the passengers summary
            WebElement paxSummary = wait.until(ExpectedConditions.visibilityOfElementLocated(paxInfoLocator));
            String paxText = paxSummary.getText();
            String lower = paxText.toLowerCase();

            Assert.assertTrue(
                    lower.contains("3") && lower.contains("adult") && lower.contains("2") && lower.contains("child"),
                    "Passengers summary did not match expected counts. Actual: " + paxText);
        } finally {
            driver.quit();
        }
    }
}
