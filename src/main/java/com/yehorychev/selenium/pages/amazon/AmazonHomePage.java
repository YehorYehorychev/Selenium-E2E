package com.yehorychev.selenium.pages.amazon;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AmazonHomePage extends BasePage {

    private static final By SEARCH_BAR = By.id("twotabsearchtextbox");
    private static final By ACCOUNTS_AND_LISTS = By.id("nav-link-accountList");
    private static final By WATCHLIST_LINK = By.xpath("//span[normalize-space()='Watchlist']");
    private static final By SIGN_IN_HEADER = By.xpath("//h1[normalize-space()='Sign in or create account']");
    private static final By AMAZON_CART_IS_EMPTY_HEADER = By.cssSelector(".a-size-large.a-spacing-top-base.sc-your-amazon-cart-is-empty");
    private static final By CART_ICON = By.cssSelector(".nav-cart-icon.nav-sprite");
    private static final By INTERSTITIAL_CONTINUE_BUTTON = By.cssSelector("form[action*='storefront'] input[type='submit'], button[name='continue']");
    private static final By INTERSTITIAL_CONTINUE_SHOPPING = By.xpath("//button[contains(.,'Continue shopping')]");
    private static final By INTERSTITIAL_HEADER = By.xpath("//h1[contains(.,'Click the button below to continue shopping')]");
    private static final By INTERSTITIAL_ANY_CONTINUE = By.xpath("//button[contains(.,'Continue')]");

    public AmazonHomePage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
        dismissInterstitialIfPresent();
    }

    private void dismissInterstitialIfPresent() {
        if (isElementPresent(INTERSTITIAL_HEADER) && isElementPresent(INTERSTITIAL_ANY_CONTINUE)) {
            log.info("Amazon interstitial (header/button) detected, continuing");
            click(INTERSTITIAL_ANY_CONTINUE);
            waitForPageReady();
            return;
        }
        if (isElementPresent(INTERSTITIAL_CONTINUE_BUTTON)) {
            log.info("Amazon interstitial detected, attempting to continue shopping");
            click(INTERSTITIAL_CONTINUE_BUTTON);
            waitForPageReady();
        } else if (isElementPresent(INTERSTITIAL_CONTINUE_SHOPPING)) {
            log.info("Amazon interstitial (button) detected, attempting to continue shopping");
            click(INTERSTITIAL_CONTINUE_SHOPPING);
            waitForPageReady();
        }
    }

    public AmazonProductResultsPage searchFor(String query) {
        if (!isElementPresent(SEARCH_BAR)) {
            dismissInterstitialIfPresent();
        }
        WebElement searchBar = find(SEARCH_BAR);
        type(SEARCH_BAR, query);
        searchBar.sendKeys(Keys.ENTER);
        waitForPageReady();
        return new AmazonProductResultsPage(driver, waitHelper);
    }

    public void hoverOverAccountsAndLists() {
        hover(ACCOUNTS_AND_LISTS);
    }

    public void openWatchlist() {
        safeClick(WATCHLIST_LINK);
    }

    public boolean isSignInHeaderDisplayed() {
        return isVisible(SIGN_IN_HEADER);
    }

    public AmazonCartPage openCartInNewWindow() {
        String parentWindow = driver.getWindowHandle();
        if (!isElementPresent(CART_ICON)) {
            dismissInterstitialIfPresent();
        }
        openLinkInNewTab(CART_ICON);
        switchToNewChildWindow();
        return new AmazonCartPage(driver, waitHelper, parentWindow);
    }
}
