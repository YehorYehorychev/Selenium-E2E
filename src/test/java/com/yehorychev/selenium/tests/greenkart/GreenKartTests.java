package com.yehorychev.selenium.tests.greenkart;

import com.yehorychev.selenium.pages.greenkart.CartOverlay;
import com.yehorychev.selenium.pages.greenkart.CheckoutPage;
import com.yehorychev.selenium.pages.greenkart.GreenKartHomePage;
import com.yehorychev.selenium.pages.greenkart.TopDealsPage;
import com.yehorychev.selenium.core.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class GreenKartTests extends BaseTest {

    private final String[] veggiesToAdd = GreenKartHomePage.defaultVegetables();

    @Test
    void addItemToCartAndApplyPromoCodeTest() {
        GreenKartHomePage homePage = new GreenKartHomePage(driver(), waitHelper());
        homePage.addVegetables(veggiesToAdd);
        Assert.assertTrue(homePage.cartContainsCount(veggiesToAdd.length),
                "All selected items were not added to the cart.");

        CartOverlay cartOverlay = homePage.openCartOverlay();
        CheckoutPage checkoutPage = cartOverlay.proceedToCheckout();

        List<String> productNames = checkoutPage.getProductNames(veggiesToAdd.length);
        String[] expectedProductNames = {"Brocolli - 1 Kg", "Cauliflower - 1 Kg", "Cucumber - 1 Kg"};

        for (String expectedProductName : expectedProductNames) {
            Assert.assertTrue(productNames.contains(expectedProductName),
                    "Product name not found in cart: " + expectedProductName);
        }

        checkoutPage.applyPromoCode("rahulshettyacademy");
        Assert.assertEquals(checkoutPage.getPromoInfoText(), "Code applied ..!",
                "Promo code was not applied successfully.");
    }

    @Test
    void verifyThatTheItemsSortedInCartTest() {
        GreenKartHomePage homePage = new GreenKartHomePage(driver(), waitHelper());
        homePage.addVegetables(veggiesToAdd);
        Assert.assertTrue(homePage.cartContainsCount(veggiesToAdd.length),
                "All selected items were not added to the cart.");

        CheckoutPage checkoutPage = homePage
                .openCartOverlay()
                .proceedToCheckout();

        List<String> productNames = checkoutPage.getProductNames(veggiesToAdd.length);
        List<String> sortedProductNames = productNames.stream()
                .sorted(String::compareToIgnoreCase)
                .toList();

        Assert.assertEquals(productNames, sortedProductNames,
                "Products in the cart are not sorted alphabetically");
    }

    @Test
    void verifyDiscountPricesTest() {
        GreenKartHomePage homePage = new GreenKartHomePage(driver(), waitHelper());
        TopDealsPage topDealsPage = homePage.openTopDeals();
        topDealsPage.sortNameColumn();

        List<String> originalList = topDealsPage.getDisplayedNames();
        List<String> sortedList = originalList.stream()
                .sorted(String::compareToIgnoreCase)
                .toList();
        Assert.assertEquals(originalList, sortedList);

        String ricePrice = topDealsPage.getPriceFor("Rice");
        Assert.assertFalse(ricePrice.isBlank(), "Price for Rice was not found");

        topDealsPage.closeAndReturn();
    }
}
