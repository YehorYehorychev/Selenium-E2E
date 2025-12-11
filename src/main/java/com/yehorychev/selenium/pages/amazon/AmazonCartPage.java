package com.yehorychev.selenium.pages.amazon;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AmazonCartPage extends BasePage {

    private static final By CART_HEADER = By.cssSelector(".a-size-large.a-spacing-top-base.sc-your-amazon-cart-is-empty");

    private final String parentWindowHandle;

    public AmazonCartPage(WebDriver driver, WaitHelper waitHelper, String parentWindowHandle) {
        super(driver, waitHelper);
        this.parentWindowHandle = parentWindowHandle;
        find(CART_HEADER);
    }

    public boolean isCartHeaderDisplayed() {
        return isVisible(CART_HEADER);
    }

    public AmazonHomePage returnToParentWindow() {
        waitHelper.switchToParentWindow(parentWindowHandle);
        return new AmazonHomePage(driver, waitHelper);
    }
}
