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

            String[] productNames = {"Brocolli", "Cauliflower", "Cucumber"};
            List<WebElement> products = driver.findElements(By.cssSelector("h4.product-name"));

            // Add "Cucumber" to the cart
            for (int i = 0; i < products.size(); i++) {
                String name = products.get(i).getText();
                if (name.contains("Cucumber")) {
                    driver.findElements(By.xpath("//button[text()='ADD TO CART']")).get(i).click();
                    break;
                }
            }

            // Verify that the item was added to the cart
            By itemsInCartLocator = By.xpath("//strong[normalize-space()='1']");
            WebElement itemsInCart = wait.until(ExpectedConditions.visibilityOfElementLocated(itemsInCartLocator));
            Assert.assertEquals(itemsInCart.getText(), "1", "Item was not added to the cart.");
        } finally {
            driver.quit();
        }
    }
}
