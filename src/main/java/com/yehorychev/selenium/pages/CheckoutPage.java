package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class CheckoutPage extends BasePage {

    private static final By PRODUCT_NAME = By.xpath("//table[@id='productCartTables']//p[@class='product-name']");
    private static final By PROMO_CODE_FIELD = By.xpath("//input[@placeholder='Enter promo code']");
    private static final By APPLY_BUTTON = By.xpath("//button[text()='Apply']");
    private static final By PROMO_INFO = By.cssSelector(".promoInfo");

    public CheckoutPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public List<String> getProductNames(int expectedCount) {
        List<WebElement> elements = waitHelper.numberOfElementsToBe(PRODUCT_NAME, expectedCount);
        return elements.stream().map(element -> element.getText().trim()).collect(Collectors.toList());
    }

    public void applyPromoCode(String promoCode) {
        waitHelper.visibilityOf(PROMO_CODE_FIELD).sendKeys(promoCode);
        waitHelper.elementToBeClickable(APPLY_BUTTON).click();
    }

    public String getPromoInfoText() {
        return waitHelper.visibilityOf(PROMO_INFO).getText().trim();
    }
}

