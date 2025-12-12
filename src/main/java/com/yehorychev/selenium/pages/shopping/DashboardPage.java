package com.yehorychev.selenium.pages.shopping;

import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends ShoppingBasePage {

    private static final By USER_GREETING = By.cssSelector(".user-name");
    private static final By PRODUCT_CARD = By.cssSelector(".card");

    public DashboardPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public boolean isLoaded() {
        waitForPageReady();
        return isVisible(PRODUCT_CARD);
    }

    public String getGreetingText() {
        return getText(USER_GREETING);
    }
}

