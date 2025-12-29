package com.yehorychev.selenium.steps.shopping;

import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.shopping.CheckoutPage;
import com.yehorychev.selenium.pages.shopping.OrderConfirmationPage;
import com.yehorychev.selenium.tests.shared.data.TestDataFactory;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Step definitions for Shopping checkout and order confirmation
 */
public class ShoppingCheckoutSteps {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCheckoutSteps.class);
    private final ScenarioContext context;
    private final WebDriver driver;
    private final WaitHelper waitHelper;
    private CheckoutPage checkoutPage;
    private OrderConfirmationPage confirmationPage;

    public ShoppingCheckoutSteps(ScenarioContext context) {
        this.context = context;
        this.driver = context.getDriver();
        this.waitHelper = context.getWaitHelper();
    }

    @When("I fill checkout form with shipping country {string}")
    public void fillCheckoutForm(String country) {
        logger.info("Filling checkout form with country: {}", country);
        checkoutPage = new CheckoutPage(driver, waitHelper);

        // Generate random customer data
        TestDataFactory.ShoppingCardDetails cardDetails = TestDataFactory.randomShoppingCard();

        checkoutPage.enterCreditCardNumber(cardDetails.cardNumber())
                   .enterCvv(cardDetails.cvv())
                   .selectExpiryMonth(cardDetails.expiryMonth())
                   .selectExpiryYear(cardDetails.expiryYear())
                   .enterName(TestDataFactory.randomFullName())
                   .selectCountry("United", country);

        logger.info("Checkout form filled with generated data");
    }

    @When("I place the order")
    public void placeOrder() {
        logger.info("Placing order");
        if (checkoutPage == null) {
            checkoutPage = new CheckoutPage(driver, waitHelper);
        }
        checkoutPage.placeOrder();
        confirmationPage = new OrderConfirmationPage(driver, waitHelper);
    }

    @Then("I should see the order confirmation")
    public void verifyOrderConfirmation() {
        logger.info("Verifying order confirmation");
        if (confirmationPage == null) {
            confirmationPage = new OrderConfirmationPage(driver, waitHelper);
        }
        assertTrue(confirmationPage.isLoaded(),
                  "Order confirmation page should be displayed");
    }

    @Then("the order should contain product {string}")
    public void verifyOrderProduct(String productName) {
        logger.info("Verifying order contains product: {}", productName);
        if (confirmationPage == null) {
            confirmationPage = new OrderConfirmationPage(driver, waitHelper);
        }
        String actualProduct = confirmationPage.getProductName();
        assertEquals(actualProduct, productName,
                    "Order should contain correct product");
    }

    @Then("the order total should be {string}")
    public void verifyOrderTotal(String expectedTotal) {
        logger.info("Verifying order total: {}", expectedTotal);
        if (confirmationPage == null) {
            confirmationPage = new OrderConfirmationPage(driver, waitHelper);
        }
        String actualTotal = confirmationPage.getProductPrice();
        assertEquals(actualTotal, expectedTotal,
                    "Order total should match");
    }
}

