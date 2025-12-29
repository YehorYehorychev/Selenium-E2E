package com.yehorychev.selenium.steps.shopping;

import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.shopping.CartPage;
import com.yehorychev.selenium.pages.shopping.DashboardPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Step definitions for Shopping cart functionality
 */
public class ShoppingCartSteps {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartSteps.class);
    private final ScenarioContext context;
    private final WebDriver driver;
    private final WaitHelper waitHelper;
    private CartPage cartPage;

    public ShoppingCartSteps(ScenarioContext context) {
        this.context = context;
        this.driver = context.getDriver();
        this.waitHelper = context.getWaitHelper();
    }

    @When("I open the cart")
    public void openCart() {
        Allure.step("Open shopping cart", () -> {
            logger.info("Opening shopping cart");
            DashboardPage dashboardPage = new DashboardPage(driver, waitHelper);
            cartPage = dashboardPage.openCart();
        });
    }

    @Then("the product {string} should be in the cart")
    public void verifyProductInCart(String productName) {
        Allure.step("Verify product in cart: " + productName, () -> {
            logger.info("Verifying product in cart: {}", productName);
            if (cartPage == null) {
                cartPage = new CartPage(driver, waitHelper);
            }
            String actualProduct = cartPage.getFirstProductName();
            assertEquals(actualProduct, productName,
                        "Product name should match");
        });
    }

    @Then("the product price should be {string}")
    public void verifyProductPrice(String expectedPrice) {
        Allure.step("Verify product price: " + expectedPrice, () -> {
            logger.info("Verifying product price: {}", expectedPrice);
            if (cartPage == null) {
                cartPage = new CartPage(driver, waitHelper);
            }
            String actualPrice = cartPage.getFirstProductPrice();
            assertEquals(actualPrice, expectedPrice,
                        "Product price should match");
        });
    }

    @When("I proceed to checkout")
    public void proceedToCheckout() {
        Allure.step("Proceed to checkout", () -> {
            logger.info("Proceeding to checkout");
            if (cartPage == null) {
                cartPage = new CartPage(driver, waitHelper);
            }
            cartPage.openCheckoutPage();
        });
    }
}

