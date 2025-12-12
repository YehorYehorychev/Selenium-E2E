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
        waitForPageReady();
        return isVisible(BUY_NOW_BUTTON) && isVisible(CHECKOUT_BUTTON);
    }

    public String getFirstProductName() {
        waitHelper.visibilityOf(PRODUCT_ROWS);
        return find(PRODUCT_NAME).getText().trim();
    }

    public String getFirstProductPrice() {
        waitHelper.visibilityOf(PRODUCT_PRICE);
        return find(PRODUCT_PRICE).getText().trim();
    }

    public String getSubtotal() {
        waitHelper.visibilityOf(PRODUCT_ROWS);
        return find(PRODUCT_PRICE).getText().trim();
    }

    public String getTotal() {
        waitHelper.visibilityOf(PRODUCT_PRICE);
        return find(PRODUCT_PRICE).getText().trim();
    }

    public CheckoutPage openCheckoutPage() {
        waitHelper.visibilityOf(CHECKOUT_BUTTON);
        click(CHECKOUT_BUTTON);
        waitHelper.waitForPageReady();
        return new CheckoutPage(driver, waitHelper);
    }
}
