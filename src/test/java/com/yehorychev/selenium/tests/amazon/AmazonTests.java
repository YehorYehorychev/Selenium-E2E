package com.yehorychev.selenium.tests.amazon;

import com.yehorychev.selenium.data.AmazonDataProviders;
import com.yehorychev.selenium.pages.amazon.AmazonCartPage;
import com.yehorychev.selenium.pages.amazon.AmazonHomePage;
import com.yehorychev.selenium.pages.amazon.AmazonProductResultsPage;
import com.yehorychev.selenium.core.BaseTest;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class AmazonTests extends BaseTest {

    @Override
    protected String getDefaultBaseUrlKey() {
        return "base.url.amazon";
    }

    @Override
    protected ChromeOptions buildChromeOptions() {
        ChromeOptions options = super.buildChromeOptions();
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-features=OptimizationGuideModelDownloading");
        options.setAcceptInsecureCerts(true);
        return options;
    }

    @Test(dataProvider = "amazonSearchKeywords", dataProviderClass = AmazonDataProviders.class)
    void amazonSearchBarTest(String keyword) {
        AmazonHomePage homePage = new AmazonHomePage(driver(), waitHelper());
        AmazonProductResultsPage resultsPage = homePage.searchFor(keyword);

        Assert.assertFalse(resultsPage.getSearchResults().isEmpty(), "Search returned no results!");
        Assert.assertEquals(resultsPage.getSearchBoxValue().toLowerCase(), keyword.toLowerCase());
    }

    @Test
    @Ignore // Ignored due to frequent UI changes on Amazon site
    void amazonActionsTest() {
        AmazonHomePage homePage = new AmazonHomePage(driver(), waitHelper());
        homePage.searchFor("laptop");

        homePage.hoverOverAccountsAndLists();
        homePage.openWatchlist();

        Assert.assertTrue(homePage.isSignInHeaderDisplayed());
    }

    @Test(dataProvider = "amazonCartAccess", dataProviderClass = AmazonDataProviders.class)
    void amazonCartInNewWindowTest(boolean expectCartVisible) {
        AmazonHomePage homePage = new AmazonHomePage(driver(), waitHelper());
        AmazonCartPage cartPage = homePage.openCartInNewWindow();

        if (expectCartVisible) {
            Assert.assertTrue(cartPage.isCartHeaderDisplayed());
        } else {
            Assert.assertFalse(cartPage.isCartHeaderDisplayed());
        }
    }
}
