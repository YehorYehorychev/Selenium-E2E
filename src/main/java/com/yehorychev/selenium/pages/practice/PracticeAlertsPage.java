package com.yehorychev.selenium.pages.practice;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PracticeAlertsPage extends BasePage {

    private static final By NAME_FIELD = By.id("name");
    private static final By CONFIRM_BUTTON = By.id("confirmbtn");
    private static final By WEB_TABLE_SECTION = By.xpath("//fieldset[2]");

    public PracticeAlertsPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public void enterName(String name) {
        type(NAME_FIELD, name);
    }

    public void triggerConfirmAlert() {
        safeClick(CONFIRM_BUTTON);
    }

    public String readAlertText() {
        return waitForAlert().getText();
    }

    public void dismissAlert() {
        driver.switchTo().alert().dismiss();
    }

    public void waitForAlertDismissed() {
        super.waitForAlertDismissed();
    }

    public WebElement getWebTableSection() {
        return find(WEB_TABLE_SECTION);
    }

    public void scrollWindowBy(int x, int y) {
        jsExecutor().executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }

    public void scrollFixedTableToBottom() {
        jsExecutor().executeScript("document.querySelector('.tableFixHead').scrollTop=5000");
    }
}
