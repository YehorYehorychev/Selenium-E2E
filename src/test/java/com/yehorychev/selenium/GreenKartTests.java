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
        } finally {
            driver.quit();
        }
    }
}
