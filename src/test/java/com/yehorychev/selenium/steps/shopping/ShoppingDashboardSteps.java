package com.yehorychev.selenium.steps.shopping;

import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.shopping.DashboardPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Step definitions for Shopping dashboard and product browsing
 */
public class ShoppingDashboardSteps {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingDashboardSteps.class);
    private final ScenarioContext context;
    private final WebDriver driver;
    private final WaitHelper waitHelper;
    private DashboardPage dashboardPage;

    public ShoppingDashboardSteps(ScenarioContext context) {
        this.context = context;
        this.driver = context.getDriver();
        this.waitHelper = context.getWaitHelper();
    }

    @Then("I should see the dashboard page")
    public void verifyDashboardPage() {
        logger.info("Verifying dashboard page");
        dashboardPage = new DashboardPage(driver, waitHelper);
        assertTrue(dashboardPage.isLoaded(), "Dashboard page should be loaded");
    }

    @When("I search for product {string}")
    public void searchForProduct(String productName) {
        logger.info("Searching for product: {}", productName);
        if (dashboardPage == null) {
            dashboardPage = new DashboardPage(driver, waitHelper);
        }
        context.set("searchedProduct", productName);
    }

    @When("I add the product to cart")
    public void addProductToCart() {
        String productName = context.get("searchedProduct");
        logger.info("Adding product to cart: {}", productName);
        if (dashboardPage == null) {
            dashboardPage = new DashboardPage(driver, waitHelper);
        }
        dashboardPage.addProductToCart(productName);
    }

    @Then("the cart count should be {string}")
    public void verifyCartCount(String expectedCount) {
        logger.info("Verifying cart count: {}", expectedCount);
        if (dashboardPage == null) {
            dashboardPage = new DashboardPage(driver, waitHelper);
        }
        int actualCount = dashboardPage.getCartCount();
        assertEquals(String.valueOf(actualCount), expectedCount,
                    "Cart count should be " + expectedCount);
    }
}

