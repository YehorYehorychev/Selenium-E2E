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
        try {
            waitForPageReady();

            // Strategy 1: Check for header + any continue button
            if (isElementPresent(INTERSTITIAL_HEADER) && isElementPresent(INTERSTITIAL_ANY_CONTINUE)) {
                log.info("Amazon interstitial (header/button) detected, continuing");
                tryClickInterstitial(INTERSTITIAL_ANY_CONTINUE);
                waitForElementToDisappear(INTERSTITIAL_HEADER);
                waitForPageReady();
                return;
            }

            // Strategy 2: Look for form submit button
            if (isElementPresent(INTERSTITIAL_CONTINUE_BUTTON)) {
                log.info("Amazon interstitial detected, attempting to continue shopping");
                tryClickInterstitial(INTERSTITIAL_CONTINUE_BUTTON);
                waitForPageReady();
                return;
            }

            // Strategy 3: Look for "Continue shopping" button
            if (isElementPresent(INTERSTITIAL_CONTINUE_SHOPPING)) {
                log.info("Amazon interstitial (button) detected, attempting to continue shopping");
                tryClickInterstitial(INTERSTITIAL_CONTINUE_SHOPPING);
                waitForPageReady();
            }
        } catch (Exception e) {
            log.warn("Failed to dismiss interstitial: {}", e.getMessage());
        }
    }

    private void tryClickInterstitial(By locator) {
        try {

            // Try JS click first (more reliable for interstitials)
            jsClick(locator);
            log.info("Successfully clicked interstitial with JS");
        } catch (Exception e) {
            log.warn("JS click failed, trying regular click: {}", e.getMessage());
            try {
                // Fallback to safe click
                safeClick(locator);
                log.info("Successfully clicked interstitial with safe click");
            } catch (Exception ex) {
                log.warn("Both click methods failed: {}", ex.getMessage());
            }
        }
    }

    public AmazonProductResultsPage searchFor(String query) {
        // Check if search bar is already visible
        if (!isElementPresent(SEARCH_BAR)) {
            log.info("Search bar not present, attempting to dismiss interstitial");
            dismissInterstitialIfPresent();

            // Double-check after dismissal with proper wait
            if (!isElementPresent(SEARCH_BAR)) {
                log.warn("Search bar still not present after interstitial dismissal, waiting for page ready...");
                waitForPageReady();
            }
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

        // Check if cart icon is visible
        if (!isElementPresent(CART_ICON)) {
            log.info("Cart icon not present, attempting to dismiss interstitial");
            dismissInterstitialIfPresent();

            // Double-check after dismissal with proper wait
            if (!isElementPresent(CART_ICON)) {
                log.warn("Cart icon still not present after interstitial dismissal, waiting for page ready...");
                waitForPageReady();
            }
        }

        openLinkInNewTab(CART_ICON);
        switchToNewChildWindow();
        return new AmazonCartPage(driver, waitHelper, parentWindow);
    }
}
