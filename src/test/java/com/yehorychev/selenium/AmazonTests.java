package com.yehorychev.selenium;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.helpers.ScreenshotHelper;
import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AmazonTests {

    @Test
    void amazonSearchBarTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        options.setAcceptInsecureCerts(true);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-features=OptimizationGuideModelDownloading");

        WebDriver driver = new ChromeDriver(options);
        WaitHelper waitHelper = new WaitHelper(driver, Duration.ofSeconds(10));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getAmazonUrl());
            Actions actions = new Actions(driver);

            By searchbarLocator = By.id("twotabsearchtextbox");

            WebElement searchbar = waitHelper.visibilityOf(searchbarLocator);
            actions.moveToElement(searchbar).click().keyDown(Keys.SHIFT).sendKeys("laptop").build().perform();

            // submit search
            searchbar.submit();

            // wait for results
            By resultsContainer = By.cssSelector("div.s-main-slot.s-result-list");
            waitHelper.visibilityOf(resultsContainer);

            // verify the results
            By resultItems = By.cssSelector("div.s-main-slot .s-result-item");
            List<WebElement> items = driver.findElements(resultItems);
            Assert.assertTrue(!items.isEmpty(), "Search returned no results!");

            // Re-locate the search bar to avoid stale
            WebElement updatedSearchbar = waitHelper.visibilityOf(searchbarLocator);
            String actualSearchValue = updatedSearchbar.getAttribute("value");
            Assert.assertEquals(actualSearchValue != null ? actualSearchValue.toLowerCase() : null, "laptop");
        } finally {
            ScreenshotHelper.capture(driver, "amazonSearchBarTest");
            driver.quit();
        }
    }

    @Test
    @Ignore // Ignored due to frequent UI changes on Amazon site
    void amazonActionsTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        options.setAcceptInsecureCerts(true);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-features=OptimizationGuideModelDownloading");

        WebDriver driver = new ChromeDriver(options);
        WaitHelper waitHelper = new WaitHelper(driver, Duration.ofSeconds(10));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getAmazonUrl());
            Actions actions = new Actions(driver);

            By searchbarLocator = By.id("twotabsearchtextbox");
            WebElement searchbar = waitHelper.visibilityOf(searchbarLocator);
            actions.moveToElement(searchbar).click().keyDown(Keys.SHIFT).sendKeys("laptop").build().perform();

            By accountsAndListsLocator = By.xpath("//div[@id='nav-link-accountList']");
            WebElement accountsAndLists = waitHelper.visibilityOf(accountsAndListsLocator);

            actions.moveToElement(accountsAndLists).build().perform();

            By watchlistLocator = By.xpath("//span[normalize-space()='Watchlist']");
            WebElement watchlist = waitHelper.elementToBeClickable(watchlistLocator);
            watchlist.click();

            By signInHeaderLocator = By.xpath("//h1[normalize-space()='Sign in or create account']");
            WebElement signInHeader = waitHelper.visibilityOf(signInHeaderLocator);

            Assert.assertTrue(signInHeader.isDisplayed());
        } finally {
            ScreenshotHelper.capture(driver, "amazonActionsTest");
            driver.quit();
        }
    }

    @Test
    void amazonCartInNewWindowTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        options.setAcceptInsecureCerts(true);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-features=OptimizationGuideModelDownloading");

        WebDriver driver = new ChromeDriver(options);
        WaitHelper waitHelper = new WaitHelper(driver, Duration.ofSeconds(10));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getAmazonUrl());
            Actions actions = new Actions(driver);

            By cartLocator = By.cssSelector(".nav-cart-icon.nav-sprite");
            WebElement cart = waitHelper.elementToBeClickable(cartLocator);
            actions.keyDown(Keys.COMMAND).moveToElement(cart).click().build().perform();

            // Switch to new window
            Set<String> windows = driver.getWindowHandles();
            Iterator<String> iterator = windows.iterator();
            String parentWindow = iterator.next();
            String childWindow = iterator.next();

            driver.switchTo().window(childWindow);

            By cartHeaderLocator = By.cssSelector(".a-size-large.a-spacing-top-base.sc-your-amazon-cart-is-empty");
            WebElement cartHeader = waitHelper.visibilityOf(cartHeaderLocator);
            Assert.assertTrue(cartHeader.isDisplayed());

            driver.switchTo().window(parentWindow);

            By parentWindowUpdateLocationLocator = By.xpath("//h2[normalize-space()='Shop gifts by category']");
            WebElement parentWindowElement = waitHelper.visibilityOf(parentWindowUpdateLocationLocator);
            Assert.assertTrue(parentWindowElement.isDisplayed());
        } finally {
            ScreenshotHelper.capture(driver, "amazonCartInNewWindowTest");
            driver.quit();
        }
    }
}
