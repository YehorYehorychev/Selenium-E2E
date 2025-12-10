package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartOverlay extends BasePage {

    private static final By PROCEED_TO_CHECKOUT = By.xpath("//button[normalize-space()='PROCEED TO CHECKOUT']");

    public CartOverlay(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public CheckoutPage proceedToCheckout() {
        waitHelper.elementToBeClickable(PROCEED_TO_CHECKOUT).click();
        return new CheckoutPage(driver, waitHelper);
    }
}

