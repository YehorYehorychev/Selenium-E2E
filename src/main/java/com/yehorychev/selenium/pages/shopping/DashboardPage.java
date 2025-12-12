package com.yehorychev.selenium.pages.shopping;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    private static final By HOME_BUTTON = By.xpath("//button[normalize-space()='HOME']");
    private static final By SIGN_OUT_BUTTON = By.xpath("//button[normalize-space()='Sign Out']");

    public DashboardPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public boolean isLoaded() {
        waitForPageReady();
        return isVisible(HOME_BUTTON) && isVisible(SIGN_OUT_BUTTON);
    }

    public void signOut() {
        click(SIGN_OUT_BUTTON);
    }
}