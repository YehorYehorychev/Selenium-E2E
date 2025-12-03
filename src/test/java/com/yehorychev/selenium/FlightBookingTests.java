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
import java.util.List;
import java.util.Objects;

public class FlightBookingTests {

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

            int adultsToAdd = 1; // 1 adult is selected by default, so add 1 more to get 2
            int childrenToAdd = 2;

            // Click adult plus button to add the required number of adults
            WebElement adultPlusBtn = wait.until(ExpectedConditions.elementToBeClickable(adultPlusBtnLocator));
            for (int i = 0; i < adultsToAdd; i++) {
                adultPlusBtn.click();
            }

            // Click child plus button to add the required number of children
            WebElement childPlusBtn = wait.until(ExpectedConditions.elementToBeClickable(childPlusBtnLocator));
            for (int i = 0; i < childrenToAdd; i++) {
                childPlusBtn.click();
            }

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

    @Test
    void flightBookingFromToCitiesTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getFlightBookingUrl());

            By departureWindowLocator = By.xpath("//input[@id='ctl00_mainContent_ddl_originStation1_CTXT']");
            By departureCityLocator = By.xpath("//a[@value='GOI']");

            // Click the departure window to open the dropdown
            WebElement departureWindow = wait.until(ExpectedConditions.elementToBeClickable(departureWindowLocator));
            departureWindow.click();

            // Wait for the city option to appear and be clickable
            WebElement departureCity = wait.until(ExpectedConditions.elementToBeClickable(departureCityLocator));
            departureCity.click();

            By arrivalWindowLocator = By.xpath("//input[@id='ctl00_mainContent_ddl_destinationStation1_CTXT']");
            By arrivalCityLocator = By.xpath("//a[@value='BLR']");

            // Click the arrival window to open the dropdown
            WebElement arrivalWindow = wait.until(ExpectedConditions.elementToBeClickable(arrivalWindowLocator));
            arrivalWindow.click();

            // Wait for the city option to appear and be clickable
            WebElement arrivalCity = wait.until(ExpectedConditions.elementToBeClickable(arrivalCityLocator));
            arrivalCity.click();

            By selectedDepartureCityLocator = By.xpath("//input[@id='ctl00_mainContent_ddl_originStation1_CTXT']");
            By selectedArrivalCityLocator = By.xpath("//input[@id='ctl00_mainContent_ddl_destinationStation1_CTXT']");

            WebElement selectedDepartureCity = wait.until(ExpectedConditions.visibilityOfElementLocated(selectedDepartureCityLocator));
            WebElement selectedArrivalCity = wait.until(ExpectedConditions.visibilityOfElementLocated(selectedArrivalCityLocator));

            Assert.assertEquals(selectedDepartureCity.getAttribute("value"), "Goa (GOI)");
            Assert.assertEquals(selectedArrivalCity.getAttribute("value"), "Bengaluru (BLR)");
        } finally {
            driver.quit();
        }
    }

    @Test
    void flightBookingDynamicDropdownTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getFlightBookingUrl());

            By searchBarLocator = By.xpath("//input[@id='autosuggest']");
            By suggestionsLocator = By.xpath("//li[@class='ui-menu-item']/a");
            String countryToSearch = "Br"; // Partial country name to trigger dynamic dropdown Virgin Islands (British)

            // Click the search bar and type the country
            WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(searchBarLocator));
            searchBar.sendKeys(countryToSearch);

            // Wait for suggestions to be loaded and visible
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(suggestionsLocator));

            // Find and click the matching country from the suggestions
            List<WebElement> suggestedCountries = driver.findElements(suggestionsLocator);
            suggestedCountries.stream()
                    .filter(webElement -> webElement.getText().equalsIgnoreCase("Virgin Islands (British)"))
                    .findFirst()
                    .ifPresent(WebElement::click);

            // Verify the selected country
            String selectedCountry = searchBar.getAttribute("value");
            Assert.assertEquals(selectedCountry, "Virgin Islands (British)");
        } finally {
            driver.quit();
        }
    }
}
