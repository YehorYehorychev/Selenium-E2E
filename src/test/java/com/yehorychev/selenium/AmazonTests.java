package com.yehorychev.selenium;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.waits.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class AmazonTests {

    @Test
    void amazonSearchBarTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
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
            Assert.assertTrue(items.size() > 0, "Search returned no results!");

            // Re-locate the search bar to avoid stale
            WebElement updatedSearchbar = waitHelper.visibilityOf(searchbarLocator);
            String actualSearchValue = updatedSearchbar.getAttribute("value");
            Assert.assertEquals(actualSearchValue.toLowerCase(), "laptop");
        } finally {
            driver.quit();
        }
    }

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
            driver.quit();
        }
    }
}
