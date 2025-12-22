package com.yehorychev.selenium.tests.amazon.ui;

import com.yehorychev.selenium.tests.amazon.data.AmazonDataProviders;
import com.yehorychev.selenium.pages.amazon.AmazonCartPage;
import com.yehorychev.selenium.pages.amazon.AmazonHomePage;
import com.yehorychev.selenium.pages.amazon.AmazonProductResultsPage;
import com.yehorychev.selenium.core.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
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
    @Severity(SeverityLevel.NORMAL)
    @Description("Validate keyword search returns non-empty, correctly echoed results on Amazon")
    void amazonSearchBarTest(String keyword) {
        AmazonHomePage homePage = openAmazonHomePage();
        AmazonProductResultsPage resultsPage = searchForKeyword(homePage, keyword);
        verifyResultsNotEmpty(resultsPage);
        verifySearchBoxValue(resultsPage, keyword);
    }

    @Test
    @Ignore // Ignored due to frequent UI changes on Amazon site
    @Severity(SeverityLevel.MINOR)
    void amazonActionsTest() {
        AmazonHomePage homePage = openAmazonHomePage();
        searchForKeyword(homePage, "laptop");
        hoverAccountsAndLists(homePage);
        openWatchlist(homePage);
        verifySignInHeader(homePage);
    }

    @Test(dataProvider = "amazonCartAccess", dataProviderClass = AmazonDataProviders.class)
    @Severity(SeverityLevel.CRITICAL)
    void amazonCartInNewWindowTest(boolean expectCartVisible) {
        AmazonHomePage homePage = openAmazonHomePage();
        AmazonCartPage cartPage = openCartInNewWindow(homePage);
        verifyCartVisibility(cartPage, expectCartVisible);
    }

    @Step("Open Amazon home page")
    private AmazonHomePage openAmazonHomePage() {
        return Allure.step("Instantiate Amazon home page", () -> new AmazonHomePage(driver(), waitHelper()));
    }

    @Step("Search for keyword: {keyword}")
    private AmazonProductResultsPage searchForKeyword(AmazonHomePage homePage, String keyword) {
        return Allure.step("Search for keyword: " + keyword, () -> homePage.searchFor(keyword));
    }

    @Step("Ensure search results list is not empty")
    private void verifyResultsNotEmpty(AmazonProductResultsPage resultsPage) {
        Allure.step("Verify search returned results", () ->
                Assert.assertFalse(resultsPage.getSearchResults().isEmpty(), "Search returned no results!"));
    }

    @Step("Ensure search box retains keyword {keyword}")
    private void verifySearchBoxValue(AmazonProductResultsPage resultsPage, String keyword) {
        Allure.step("Verify search box retains keyword", () ->
                Assert.assertEquals(resultsPage.getSearchBoxValue().toLowerCase(), keyword.toLowerCase()));
    }

    @Step("Hover over Accounts & Lists")
    private void hoverAccountsAndLists(AmazonHomePage homePage) {
        Allure.step("Hover over Accounts & Lists", homePage::hoverOverAccountsAndLists);
    }

    @Step("Open Watchlist")
    private void openWatchlist(AmazonHomePage homePage) {
        Allure.step("Open Watchlist", homePage::openWatchlist);
    }

    @Step("Verify Sign-In header is displayed")
    private void verifySignInHeader(AmazonHomePage homePage) {
        Allure.step("Check Sign-In header visibility", () ->
                Assert.assertTrue(homePage.isSignInHeaderDisplayed()));
    }

    @Step("Open cart in new window")
    private AmazonCartPage openCartInNewWindow(AmazonHomePage homePage) {
        return Allure.step("Open cart in new window", homePage::openCartInNewWindow);
    }

    @Step("Validate cart visibility expectation")
    private void verifyCartVisibility(AmazonCartPage cartPage, boolean expectVisible) {
        Allure.step("Verify cart visibility matches expectation", () -> {
            if (expectVisible) {
                Assert.assertTrue(cartPage.isCartHeaderDisplayed());
            } else {
                Assert.assertFalse(cartPage.isCartHeaderDisplayed());
            }
        });
    }
}
