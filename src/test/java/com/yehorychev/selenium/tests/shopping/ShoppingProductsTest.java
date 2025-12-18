package com.yehorychev.selenium.tests.shopping;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.core.ShoppingAuthenticatedBaseTest;
import com.yehorychev.selenium.data.ShoppingDataProviders;
import com.yehorychev.selenium.data.ShoppingTestData;
import com.yehorychev.selenium.pages.shopping.CartPage;
import com.yehorychev.selenium.pages.shopping.CheckoutPage;
import com.yehorychev.selenium.pages.shopping.DashboardPage;
import com.yehorychev.selenium.pages.shopping.OrderConfirmationPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ShoppingProductsTest extends ShoppingAuthenticatedBaseTest {

    @Test(dataProvider = "shoppingProducts", dataProviderClass = ShoppingDataProviders.class)
    public void shouldAllowUserToAddProductToCartAndCheckout(ShoppingTestData dataSet) {
        ShoppingTestData testData = dataSet.withCardDetails(
                ConfigProperties.getShoppingCardNumber(),
                ConfigProperties.getShoppingCardCvv()
        );

        DashboardPage dashboardPage = openDashboard();
        Assert.assertTrue(dashboardPage.isLoaded(), "Dashboard page is not loaded properly");
        Assert.assertTrue(dashboardPage.containsProduct(testData.productName()), testData.productName() + " should be present in products list");

        dashboardPage.addProductToCart(testData.productName());
        dashboardPage.waitForCartCount(1);
        Assert.assertEquals(dashboardPage.getCartCount(), 1, "Cart badge should show 1 item");

        CartPage cartPage = dashboardPage.openCart();
        Assert.assertTrue(cartPage.isLoaded(), "Cart page failed to load");
        Assert.assertEquals(cartPage.getFirstProductName(), testData.productName(), "Unexpected product in cart");
        Assert.assertEquals(cartPage.getFirstProductPrice(), testData.productPrice(), "Unexpected product price");
        Assert.assertEquals(cartPage.getTotal(), testData.productPrice(), "Total should match product price");

        CheckoutPage checkoutPage = cartPage.openCheckoutPage();
        Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page should be visible");
        Assert.assertEquals(checkoutPage.getProductName(), testData.productName());
        Assert.assertEquals(checkoutPage.getProductPrice(), testData.productPrice());

        checkoutPage
                .enterCreditCardNumber(testData.cardNumber())
                .enterCvv(testData.cvv())
                .enterName(testData.cardHolderName())
                .selectExpiryMonth(testData.expiryMonth())
                .selectExpiryYear(testData.expiryYear())
                .selectCountry(testData.countryQuery(), testData.countryToSelect());

        checkoutPage.placeOrder();

        OrderConfirmationPage confirmationPage = new OrderConfirmationPage(driver(), waitHelper());
        Assert.assertTrue(confirmationPage.isLoaded(), "Order confirmation page should load");
        Assert.assertEquals(confirmationPage.getProductName(), testData.productName());
        Assert.assertEquals(confirmationPage.getProductPrice(), testData.productPrice());
        Assert.assertTrue(confirmationPage.isThankYouMessageDisplayed(), "Thank you message should be visible");
        Assert.assertTrue(confirmationPage.isDownloadButtonVisible(), "Download button should be visible");
        Assert.assertFalse(confirmationPage.getOrderNumber().isBlank(), "Order number must be present");
    }
}