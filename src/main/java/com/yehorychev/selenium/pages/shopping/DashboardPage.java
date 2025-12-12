package com.yehorychev.selenium.pages.shopping;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DashboardPage extends BasePage {

    private static final By HOME_BUTTON = By.xpath("//button[normalize-space()='HOME']");
    private static final By SIGN_OUT_BUTTON = By.xpath("//button[normalize-space()='Sign Out']");
    private static final By PRODUCTS_LIST = By.cssSelector(".mb-3");
    private static final By PRODUCT_NAME = By.cssSelector("b");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector(".card-body button:last-of-type");
    private static final By CART_BUTTON = By.cssSelector("button[routerlink='/dashboard/cart']");
    private static final By CART_BADGE = By.cssSelector("button[routerlink='/dashboard/cart'] label");

    public DashboardPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public boolean isLoaded() {
        waitForPageReady();
        return isVisible(HOME_BUTTON) && isVisible(SIGN_OUT_BUTTON);
    }

    public List<WebElement> getProductsList() {
        waitForAjaxComplete();
        return findAll(PRODUCTS_LIST);
    }

    public List<String> getProductNames() {
        return getProductsList().stream()
                .map(card -> card.findElement(PRODUCT_NAME).getText().trim())
                .toList();
    }

    public boolean containsProduct(String productName) {
        return getProductNames().stream()
                .anyMatch(name -> name.equalsIgnoreCase(productName));
    }

    private WebElement findProductCard(String productName) {
        return getProductsList().stream()
                .filter(card -> card.findElement(PRODUCT_NAME).getText().trim().equalsIgnoreCase(productName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Product not found: " + productName));
    }

    public void addProductToCart(String productName) {
        WebElement productCard = findProductCard(productName);
        WebElement addButton = productCard.findElement(ADD_TO_CART_BUTTON);
        log.info("Adding product '{}' to cart", productName);
        addButton.click();
        waitHelper.waitForAjaxComplete();
    }

    public int getCartCount() {
        try {
            String text = find(CART_BADGE).getText().trim();
            return Integer.parseInt(text);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public void waitForCartCount(int expectedCount) {
        waitHelper.waitForTextEquals(CART_BADGE, String.valueOf(expectedCount));
    }

    public void signOut() {
        click(SIGN_OUT_BUTTON);
    }
}