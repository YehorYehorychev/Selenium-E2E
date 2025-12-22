package com.yehorychev.selenium.tests.shopping.ui;

import com.yehorychev.selenium.core.ShoppingAuthenticatedBaseTest;
import com.yehorychev.selenium.tests.shopping.data.ShoppingDataProviders;
import com.yehorychev.selenium.tests.shopping.data.ShoppingTestData;
import com.yehorychev.selenium.tests.shared.data.TestDataFactory;
import com.yehorychev.selenium.tests.shared.data.TestDataFactory.ShoppingCardDetails;
import com.yehorychev.selenium.pages.shopping.CartPage;
import com.yehorychev.selenium.pages.shopping.CheckoutPage;
import com.yehorychev.selenium.pages.shopping.DashboardPage;
import com.yehorychev.selenium.pages.shopping.OrderConfirmationPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ShoppingProductsTest extends ShoppingAuthenticatedBaseTest {

    @Test(dataProvider = "shoppingProducts", dataProviderClass = ShoppingDataProviders.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("End-to-end purchase flow that adds a product to cart and completes checkout")
    public void shouldAllowUserToAddProductToCartAndCheckout(ShoppingTestData dataSet) {
        annotateDataSet(String.format("Dataset: %s (%s)", dataSet.productName(), dataSet.productPrice()), SeverityLevel.CRITICAL);
        ShoppingTestData testData = dataSet;

        DashboardPage dashboardPage = openDashboard();
        verifyDashboardLoaded(dashboardPage);
        Assert.assertTrue(dashboardPage.containsProduct(testData.productName()), testData.productName() + " should be present in products list");

        addItemToCart(dashboardPage, testData);
        CartPage cartPage = dashboardPage.openCart();
        verifyCart(cartPage, testData);

        CheckoutPage checkoutPage = cartPage.openCheckoutPage();
        fillCheckoutForm(checkoutPage, testData);

        OrderConfirmationPage confirmationPage = new OrderConfirmationPage(driver(), waitHelper());
        verifyOrderConfirmation(confirmationPage, testData);
    }


    @Step("Verify dashboard page is loaded")
    private void verifyDashboardLoaded(DashboardPage dashboardPage) {
        Allure.step("Verify dashboard page is loaded", () ->
                Assert.assertTrue(dashboardPage.isLoaded(), "Dashboard page is not loaded properly")
        );
    }

    @Step("Add product to cart")
    private void addItemToCart(DashboardPage dashboardPage, ShoppingTestData testData) {
        Allure.step("Add %s to cart".formatted(testData.productName()), () -> {
            dashboardPage.addProductToCart(testData.productName());
            dashboardPage.waitForCartCount(1);
            Assert.assertEquals(dashboardPage.getCartCount(), 1, "Cart badge should show 1 item");
        });
    }

    @Step("Verify cart has expected product")
    private void verifyCart(CartPage cartPage, ShoppingTestData testData) {
        Allure.step("Verify cart has expected product", () -> {
            Assert.assertTrue(cartPage.isLoaded(), "Cart page failed to load");
            Assert.assertEquals(cartPage.getFirstProductName(), testData.productName(), "Unexpected product in cart");
            Assert.assertEquals(cartPage.getFirstProductPrice(), testData.productPrice(), "Unexpected product price");
            Assert.assertEquals(cartPage.getTotal(), testData.productPrice(), "Total should match product price");
        });
    }

    @Step("Fill checkout form and place order")
    private void fillCheckoutForm(CheckoutPage checkoutPage, ShoppingTestData testData) {
        TestDataFactory.UserProfile customerProfile = TestDataFactory.randomUser();
        String customerFullName = "%s %s".formatted(customerProfile.firstName(), customerProfile.lastName());
        ShoppingCardDetails paymentDetails = TestDataFactory.randomShoppingCard();
        Allure.step("Fill checkout form and place order", () -> {
            Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page should be visible");
            Assert.assertEquals(checkoutPage.getProductName(), testData.productName());
            Assert.assertEquals(checkoutPage.getProductPrice(), testData.productPrice());

            checkoutPage
                    .enterCreditCardNumber(paymentDetails.cardNumber())
                    .enterCvv(paymentDetails.cvv())
                    .enterName(customerFullName)
                    .selectExpiryMonth(paymentDetails.expiryMonth())
                    .selectExpiryYear(paymentDetails.expiryYear())
                    .selectCountry(testData.countryQuery(), testData.countryToSelect())
                    .placeOrder();
            Allure.addAttachment("Checkout customer", customerFullName);
        });
    }

    @Step("Validate order confirmation")
    private void verifyOrderConfirmation(OrderConfirmationPage confirmationPage, ShoppingTestData testData) {
        Allure.step("Validate order confirmation", () -> {
            Assert.assertTrue(confirmationPage.isLoaded(), "Order confirmation page should load");
            Assert.assertEquals(confirmationPage.getProductName(), testData.productName());
            Assert.assertEquals(confirmationPage.getProductPrice(), testData.productPrice());
            Assert.assertTrue(confirmationPage.isThankYouMessageDisplayed(), "Thank you message should be visible");
            Assert.assertTrue(confirmationPage.isDownloadButtonVisible(), "Download button should be visible");
            Assert.assertFalse(confirmationPage.getOrderNumber().isBlank(), "Order number must be present");
        });
    }
}