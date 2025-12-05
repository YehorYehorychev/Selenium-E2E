package com.yehorychev.selenium;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class GreenKartTests {

    @Test
    void addItemToCartAndApplyPromoCodeTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WaitHelper waitHelper = new WaitHelper(driver, Duration.ofSeconds(5));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getGreenKartUrl());

            String[] veggiesToAdd = {"Brocolli", "Cauliflower", "Cucumber"};
            By cartCountLocator = By.cssSelector("div.cart-info td:nth-child(3) strong");

            // Add all three vegetables to the cart
            for (int i = 0; i < veggiesToAdd.length; i++) {
                String veggie = veggiesToAdd[i];
                WebElement addToCartButton = driver.findElement(By.xpath("//h4[contains(text(), '" + veggie + "')]/following::button[text()='ADD TO CART']"));
                addToCartButton.click();
                waitHelper.waitForCartCount(cartCountLocator, i + 1);
            }

            WebElement itemsInCart = waitHelper.waitForCartCount(cartCountLocator, veggiesToAdd.length);
            Assert.assertEquals(itemsInCart.getText().trim(), String.valueOf(veggiesToAdd.length), "All selected items were not added to the cart.");

            // Click on the cart icon to view the cart
            By cartIconLocator = By.xpath("//img[@alt='Cart']");
            WebElement cartIcon = waitHelper.elementToBeClickable(cartIconLocator);
            cartIcon.click();

            // Click on the "PROCEED TO CHECKOUT" button
            By proceedToCheckoutLocator = By.xpath("//button[normalize-space()='PROCEED TO CHECKOUT']");
            WebElement proceedToCheckoutButton = waitHelper.elementToBeClickable(proceedToCheckoutLocator);
            proceedToCheckoutButton.click();

            // Wait for the product names to be visible on the checkout page
            By productNameLocator = By.xpath("//table[@id='productCartTables']//p[@class='product-name']");
            List<WebElement> productNames = waitHelper.numberOfElementsToBe(productNameLocator, veggiesToAdd.length);

            // Verify that the expected products are displayed
            Assert.assertEquals(productNames.size(), veggiesToAdd.length, "Unexpected number of products in the cart");

            String[] expectedProductNames = {"Brocolli - 1 Kg", "Cauliflower - 1 Kg", "Cucumber - 1 Kg"};
            List<String> actualProductNames = productNames.stream()
                    .map(element -> element.getText().trim())
                    .collect(Collectors.toList());

            for (String expectedProductName : expectedProductNames) {
                Assert.assertTrue(actualProductNames.contains(expectedProductName),
                        "Product name not found in cart: " + expectedProductName);
            }

            // Apply promo code
            By promoCodeFieldLocator = By.xpath("//input[@placeholder='Enter promo code']");
            WebElement promoCodeField = waitHelper.visibilityOf(promoCodeFieldLocator);
            promoCodeField.sendKeys("rahulshettyacademy");

            // Click on the "Apply" button
            By applyButtonLocator = By.xpath("//button[text()='Apply']");
            WebElement applyButton = waitHelper.elementToBeClickable(applyButtonLocator);
            applyButton.click();

            // Verify that the promo code was applied successfully
            By promoInfoLocator = By.cssSelector(".promoInfo");
            WebElement promoInfo = waitHelper.visibilityOf(promoInfoLocator);
            Assert.assertEquals(promoInfo.getText().trim(), "Code applied ..!", "Promo code was not applied successfully.");
        } finally {
            driver.quit();
        }
    }
}
