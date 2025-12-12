package com.yehorychev.selenium.pages.shopping;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OrderConfirmationPage extends BasePage {

    private static final By PRODUCT_NAME = By.xpath("//div[normalize-space()='ZARA COAT 3']");
    private static final By PRODUCT_PRICE = By.xpath("//div[normalize-space()='$ 11500']");
    private static final By CLICK_TO_DOWNLOAD_ORDER_BUTTON = By.cssSelector(".btn.btn-primary.mt-3.mb-3");
    private static final By THANK_YOU_FOR_THE_ORDER_TEXT = By.cssSelector(".hero-primary");
    private static final By ORDER_NUMBER_TEXT = By.cssSelector("label.ng-star-inserted");
    private static final By TEMP_TOAST_MESSAGE = By.cssSelector("#toast-container");

    public OrderConfirmationPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public boolean isLoaded() {
        waitForPageReady();
        waitHelper.visibilityOf(TEMP_TOAST_MESSAGE);
        waitHelper.waitForElementToDisappear(TEMP_TOAST_MESSAGE);
        waitHelper.visibilityOf(THANK_YOU_FOR_THE_ORDER_TEXT);
        waitHelper.visibilityOf(ORDER_NUMBER_TEXT);
        waitHelper.visibilityOf(CLICK_TO_DOWNLOAD_ORDER_BUTTON);
        return true;
    }

    public String getProductName() {
        waitHelper.visibilityOf(PRODUCT_NAME);
        return getText(PRODUCT_NAME);
    }

    public String getProductPrice() {
        waitHelper.visibilityOf(PRODUCT_PRICE);
        return getText(PRODUCT_PRICE);
    }

    public String getOrderNumber() {
        waitHelper.visibilityOf(ORDER_NUMBER_TEXT);
        return getText(ORDER_NUMBER_TEXT).trim();
    }

    public boolean isThankYouMessageDisplayed() {
        return isVisible(THANK_YOU_FOR_THE_ORDER_TEXT);
    }

    public boolean isDownloadButtonVisible() {
        return isVisible(CLICK_TO_DOWNLOAD_ORDER_BUTTON);
    }
}
