package com.yehorychev.selenium.pages.shopping;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

    private static final By PRODUCT_ROWS = By.cssSelector("div.cartSection");
    private static final By PRODUCT_NAME = By.cssSelector("div.cartSection h3");
    private static final By PRODUCT_PRICE = By.cssSelector("div[class='prodTotal cartSection'] p");
    private static final By BUY_NOW_BUTTON = By.xpath("//button[normalize-space()='Buy Now']");
    private static final By CHECKOUT_BUTTON = By.xpath("//button[normalize-space()='Checkout']");

    public CartPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public boolean isLoaded() {
        waitForPageReadyAndAjax();
        return isVisible(BUY_NOW_BUTTON) && isVisible(CHECKOUT_BUTTON);
    }

    public String getFirstProductName() {
        waitForPageReady();
        return getText(PRODUCT_NAME).trim();
    }

    public String getFirstProductPrice() {
        waitForPageReady();
        return getText(PRODUCT_PRICE).trim();
    }

    public String getSubtotal() {
        waitForPageReady();
        return getText(PRODUCT_PRICE).trim();
    }

    public String getTotal() {
        waitForPageReady();
        return getText(PRODUCT_PRICE).trim();
    }

    public CheckoutPage openCheckoutPage() {
        waitForPageReady();
        safeClick(CHECKOUT_BUTTON);
        waitForPageReady();
        return new CheckoutPage(driver, waitHelper);
    }
}
