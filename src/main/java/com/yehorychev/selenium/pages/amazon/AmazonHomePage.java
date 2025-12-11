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
    private static final By SHOP_GIFT_CATEGORY_HEADER = By.xpath("//h2[normalize-space()='Shop gifts by category']");
    private static final By CART_ICON = By.cssSelector(".nav-cart-icon.nav-sprite");

    public AmazonHomePage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public AmazonProductResultsPage searchFor(String query) {
        WebElement searchBar = find(SEARCH_BAR);
        actions().moveToElement(searchBar).click().keyDown(Keys.SHIFT).sendKeys(query).keyUp(Keys.SHIFT).perform();
        searchBar.submit();
        return new AmazonProductResultsPage(driver, waitHelper);
    }

    public void hoverOverAccountsAndLists() {
        WebElement element = find(ACCOUNTS_AND_LISTS);
        actions().moveToElement(element).perform();
    }

    public void openWatchlist() {
        click(WATCHLIST_LINK);
    }

    public boolean isSignInHeaderDisplayed() {
        return isVisible(SIGN_IN_HEADER);
    }

    public AmazonCartPage openCartInNewWindow() {
        String parentWindow = driver.getWindowHandle();
        WebElement cart = find(CART_ICON);
        actions().keyDown(platformControlKey()).moveToElement(cart).click().keyUp(platformControlKey()).perform();
        waitHelper.switchToNewChildWindow();
        return new AmazonCartPage(driver, waitHelper, parentWindow);
    }

    public boolean isShopGiftCategoryVisible() {
        driver.switchTo().window(driver.getWindowHandle());
        waitForPageReady();
        return isVisible(SHOP_GIFT_CATEGORY_HEADER);
    }
}
