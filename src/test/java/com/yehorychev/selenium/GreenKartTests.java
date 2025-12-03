package com.yehorychev.selenium;

import com.yehorychev.selenium.config.ConfigProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class GreenKartTests {

    @Test
    void addItemToCartTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            driver.manage().window().maximize();
            driver.navigate().to(ConfigProperties.getGreenKartUrl());

            String[] veggiesToAdd = {"Cucumber", "Brocolli", "Beetroot"};

            // Add all three vegetables to the cart
            for (String veggie : veggiesToAdd) {
                WebElement addToCartButton = driver.findElement(By.xpath("//h4[contains(text(), '" + veggie + "')]/following::button[text()='ADD TO CART']"));
                addToCartButton.click();
            }

            // Verify that all 3 items were added to the cart
            By itemsInCartLocator = By.xpath("//strong[normalize-space()='3']");
            WebElement itemsInCart = wait.until(ExpectedConditions.visibilityOfElementLocated(itemsInCartLocator));
            Assert.assertEquals(itemsInCart.getText().trim(), "3", "All 3 items were not added to the cart.");

            // Click on the cart icon to view the cart
            By cartIconLocator = By.cssSelector(".cart-icon");
            WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(cartIconLocator));
            cartIcon.click();

            // Click on the "PROCEED TO CHECKOUT" button
            By proceedToCheckoutLocator = By.xpath("//button[normalize-space()='PROCEED TO CHECKOUT']");
            WebElement proceedToCheckoutButton = wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckoutLocator));
            proceedToCheckoutButton.click();

            List<WebElement> productNames = driver.findElements(By.xpath("//p[@class='product-name']"));

            // Verify that 3 products are displayed
            Assert.assertEquals(productNames.size(), 3, "Expected 3 products in the cart");

            // Verify the product names match the expected values
            String[] expectedProductNames = {"Broccoli - 1 Kg", "Cauliflower - 1 Kg", "Cucumber - 1 Kg"};
            for (int i = 0; i < expectedProductNames.length; i++) {
                Assert.assertEquals(productNames.get(i).getText().trim(), expectedProductNames[i],
                        "Product name at index " + i + " does not match");
            }
        } finally {
            driver.quit();
        }
    }
}
