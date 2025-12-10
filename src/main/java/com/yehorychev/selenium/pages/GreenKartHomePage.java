package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

public class GreenKartHomePage extends BasePage {

    private static final By CART_COUNT_LOCATOR = By.cssSelector("div.cart-info td:nth-child(3) strong");
    private static final By CART_ICON = By.xpath("//img[@alt='Cart']");
    private static final By TOP_DEALS = By.xpath("//a[normalize-space()='Top Deals']");

    public GreenKartHomePage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public void addVegetables(String... names) {
        if (names == null) {
            return;
        }
        for (int i = 0; i < names.length; i++) {
            String veggie = names[i];
            By addToCartButton = By.xpath("//h4[contains(text(), '" + veggie + "')]/following::button[text()='ADD TO CART']");
            WebElement button = driver.findElement(addToCartButton);
            button.click();
            waitHelper.waitForCartCount(CART_COUNT_LOCATOR, i + 1);
        }
    }

    public boolean cartContainsCount(int expectedCount) {
        WebElement element = waitHelper.waitForCartCount(CART_COUNT_LOCATOR, expectedCount);
        return String.valueOf(expectedCount).equals(element.getText().trim());
    }

    public CartOverlay openCartOverlay() {
        waitHelper.elementToBeClickable(CART_ICON).click();
        return new CartOverlay(driver, waitHelper);
    }

    public TopDealsPage openTopDeals() {
        String parentWindow = driver.getWindowHandle();
        waitHelper.elementToBeClickable(TOP_DEALS).click();
        waitHelper.switchToNewChildWindow();
        return new TopDealsPage(driver, waitHelper, parentWindow);
    }

    public static String[] defaultVegetables() {
        return new String[]{"Brocolli", "Cauliflower", "Cucumber"};
    }
}

