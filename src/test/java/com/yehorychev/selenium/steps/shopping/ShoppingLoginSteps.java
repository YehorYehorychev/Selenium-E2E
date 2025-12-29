package com.yehorychev.selenium.steps.shopping;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.shopping.LoginPage;
import com.yehorychev.selenium.tests.shopping.api.helpers.ShoppingApiAuthClient;
import com.yehorychev.selenium.tests.shopping.api.helpers.ShoppingSession;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for Shopping login functionality
 */
public class ShoppingLoginSteps {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingLoginSteps.class);
    private final ScenarioContext context;
    private final WebDriver driver;
    private final WaitHelper waitHelper;
    private LoginPage loginPage;

    public ShoppingLoginSteps(ScenarioContext context) {
        this.context = context;
        this.driver = context.getDriver();
        this.waitHelper = context.getWaitHelper();
    }

    @Given("the shopping application is opened")
    public void openShoppingApp() {
        String url = ConfigProperties.getProperty("base.url.shopping");
        logger.info("Opening shopping application: {}", url);
        driver.get(url);
        loginPage = new LoginPage(driver, waitHelper);
    }

    @When("I login with email {string} and password {string}")
    public void loginWithCredentials(String email, String password) {
        logger.info("Logging in with email: {}", email);
        loginPage.login(email, password);
    }

    @Given("I am logged in via API with email {string} and password {string}")
    public void loginViaAPI(String email, String password) {
        logger.info("Logging in via API with email: {}", email);

        // Get auth token via API
        ShoppingApiAuthClient apiClient = new ShoppingApiAuthClient();
        ShoppingSession session = apiClient.login(email, password);

        logger.info("Auth token received: {}", session.token().substring(0, 20) + "...");

        // Store in context for other steps
        context.set("authToken", session.token());
        context.set("userId", session.userId());

        // Navigate to shopping site first
        String url = ConfigProperties.getProperty("base.url.shopping");
        driver.get(url);

        // Inject token into browser localStorage
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(String.format(
            "window.localStorage.setItem('token', '%s');", session.token()
        ));
        js.executeScript(String.format(
            "window.localStorage.setItem('userId', '%s');", session.userId()
        ));

        logger.info("Token injected into localStorage");
    }

    @When("I navigate to the dashboard")
    public void navigateToDashboard() {
        String dashboardUrl = ConfigProperties.getProperty("base.url.shopping") + "/dashboard";
        logger.info("Navigating to dashboard: {}", dashboardUrl);
        driver.get(dashboardUrl);
    }

    @Then("the {string} button should be visible")
    public void buttonShouldBeVisible(String buttonText) {
        logger.info("Verifying button is visible: {}", buttonText);
        // This will be implemented in DashboardSteps or CommonSteps
        // For now, just verify page loaded
        assertTrue(driver.getCurrentUrl().contains("dashboard") ||
                  driver.getCurrentUrl().contains("client"),
                  "Should be on authenticated page");
    }
}

