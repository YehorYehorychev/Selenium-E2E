package com.yehorychev.selenium.pages.shopping;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By EMAIL_INPUT = By.cssSelector("#userEmail");
    private static final By PASSWORD_INPUT = By.cssSelector("#userPassword");
    private static final By LOGIN_BUTTON = By.cssSelector("#login");

    public LoginPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public LoginPage enterEmail(String email) {
        type(EMAIL_INPUT, email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(PASSWORD_INPUT, password);
        return this;
    }

    public DashboardPage submit() {
        click(LOGIN_BUTTON);
        waitForPageReady();
        return new DashboardPage(driver, waitHelper);
    }

    public DashboardPage login(String email, String password) {
        return enterEmail(email)
                .enterPassword(password)
                .submit();
    }
}