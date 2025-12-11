package com.yehorychev.selenium.pages.greenkart;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartOverlay extends BasePage {

    private static final By PROCEED_TO_CHECKOUT = By.xpath("//button[normalize-space()='PROCEED TO CHECKOUT']");

    public CartOverlay(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public CheckoutPage proceedToCheckout() {
        click(PROCEED_TO_CHECKOUT);
        return new CheckoutPage(driver, waitHelper);
    }
}
