package com.yehorychev.selenium.pages.shopping;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class CheckoutPage extends BasePage {

    private static final By PRODUCT_SECTION = By.cssSelector(".payment__info");
    private static final By PRODUCT_NAME = By.cssSelector(".item__title");
    private static final By PRODUCT_PRICE = By.cssSelector(".item__price");
    private static final By SELECT_COUNTRY_FIELD = By.cssSelector("input[placeholder='Select Country']");
    private static final By COUNTRY_OPTIONS = By.cssSelector("section.ta-results button span");
    private static final By CREDIT_CARD_NUMBER_TEXT_FIELD = By.cssSelector("input.input.txt.text-validated");
    private static final By CVV_CODE_TEXT_FIELD = By.xpath("(//input[@type='text'])[2]");
    private static final By CARD_EXPIRATION_FROM = By.xpath("//body//app-root//select[1]");
    private static final By CARD_EXPIRATION_TO = By.xpath("(//select[@class='input ddl'])[2]");
    private static final By CUSTOMER_NAME_TEXT_FIELD = By.xpath("(//input[@type='text'])[3]");
    private static final By APPLY_COUPON_TEXT_FIELD = By.cssSelector("input[name='coupon']");
    private static final By APPLY_COUPON_BUTTON = By.cssSelector("button[type='submit']");
    private static final By PLACE_ORDER_BUTTON = By.cssSelector(".btnn.action__submit.ng-star-inserted");

    public CheckoutPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public boolean isLoaded() {
        waitForPageReady();
        return isVisible(PRODUCT_SECTION) && isVisible(CREDIT_CARD_NUMBER_TEXT_FIELD);
    }

    public String getProductName() {
        waitHelper.visibilityOf(PRODUCT_SECTION);
        return getText(PRODUCT_NAME);
    }

    public String getProductPrice() {
        waitHelper.visibilityOf(PRODUCT_SECTION);
        return getText(PRODUCT_PRICE);
    }

    public CheckoutPage enterCreditCardNumber(String cardNumber) {
        type(CREDIT_CARD_NUMBER_TEXT_FIELD, cardNumber);
        return this;
    }

    public CheckoutPage enterCvv(String cvv) {
        type(CVV_CODE_TEXT_FIELD, cvv);
        return this;
    }

    public CheckoutPage enterName(String name) {
        type(CUSTOMER_NAME_TEXT_FIELD, name);
        return this;
    }

    public CheckoutPage enterCoupon(String couponCode) {
        type(APPLY_COUPON_TEXT_FIELD, couponCode);
        return this;
    }

    public CheckoutPage applyCoupon() {
        click(APPLY_COUPON_BUTTON);
        return this;
    }

    public CheckoutPage selectExpiryMonth(String monthValue) {
        new Select(find(CARD_EXPIRATION_FROM)).selectByVisibleText(monthValue);
        return this;
    }

    public CheckoutPage selectExpiryYear(String yearValue) {
        new Select(find(CARD_EXPIRATION_TO)).selectByVisibleText(yearValue);
        return this;
    }

    public CheckoutPage selectCountry(String query, String optionToSelect) {
        type(SELECT_COUNTRY_FIELD, query);
        waitHelper.visibilityOf(COUNTRY_OPTIONS);
        List<WebElement> options = findAll(COUNTRY_OPTIONS);
        WebElement match = options.stream()
                .filter(element -> element.getText().trim().equalsIgnoreCase(optionToSelect))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Country option not found: " + optionToSelect));
        match.click();
        return this;
    }

    public void placeOrder() {
        waitHelper.visibilityOf(PLACE_ORDER_BUTTON);
        click(PLACE_ORDER_BUTTON);
    }
}
