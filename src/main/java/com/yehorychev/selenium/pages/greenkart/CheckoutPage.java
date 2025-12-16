package com.yehorychev.selenium.pages.greenkart;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
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
        return waitForElementsCount(PRODUCT_NAME, expectedCount).stream()
                .map(element -> element.getText().trim())
                .collect(Collectors.toList());
    }

    public void applyPromoCode(String promoCode) {
        type(PROMO_CODE_FIELD, promoCode);
        click(APPLY_BUTTON);
    }

    public String getPromoInfoText() {
        return getText(PROMO_INFO).trim();
    }
}
