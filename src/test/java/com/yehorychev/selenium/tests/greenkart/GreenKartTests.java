package com.yehorychev.selenium.tests.greenkart;

import com.yehorychev.selenium.data.GreenKartDataProviders;
import com.yehorychev.selenium.data.GreenKartDataProviders.TopDealsData;
import com.yehorychev.selenium.data.GreenKartTestData;
import com.yehorychev.selenium.pages.greenkart.CartOverlay;
import com.yehorychev.selenium.pages.greenkart.CheckoutPage;
import com.yehorychev.selenium.pages.greenkart.GreenKartHomePage;
import com.yehorychev.selenium.pages.greenkart.TopDealsPage;
import com.yehorychev.selenium.core.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class GreenKartTests extends BaseTest {

    @Test(dataProvider = "greenKartCartScenarios", dataProviderClass = GreenKartDataProviders.class)
    void addItemToCartAndApplyPromoCodeTest(GreenKartTestData testData) {
        String[] veggiesToAdd = testData.vegetables().toArray(String[]::new);
        GreenKartHomePage homePage = new GreenKartHomePage(driver(), waitHelper());
        homePage.addVegetables(veggiesToAdd);
        Assert.assertTrue(homePage.cartContainsCount(veggiesToAdd.length),
                "All selected items were not added to the cart.");

        CartOverlay cartOverlay = homePage.openCartOverlay();
        CheckoutPage checkoutPage = cartOverlay.proceedToCheckout();

        List<String> productNames = checkoutPage.getProductNames(veggiesToAdd.length);
        for (String expectedProductName : testData.expectedCartNames()) {
            Assert.assertTrue(productNames.contains(expectedProductName),
                    "Product name not found in cart: " + expectedProductName);
        }

        checkoutPage.applyPromoCode(testData.promoCode());
        Assert.assertEquals(checkoutPage.getPromoInfoText(), "Code applied ..!",
                "Promo code was not applied successfully.");
    }

    @Test(dataProvider = "greenKartCartSorting", dataProviderClass = GreenKartDataProviders.class)
    void verifyThatTheItemsSortedInCartTest(GreenKartTestData testData) {
        String[] veggiesToAdd = testData.vegetables().toArray(String[]::new);
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

        if (testData.expectSorted()) {
            Assert.assertEquals(productNames, sortedProductNames,
                    "Products in the cart are not sorted alphabetically");
        }
    }

    @Test(dataProvider = "greenKartTopDeals", dataProviderClass = GreenKartDataProviders.class)
    void verifyDiscountPricesTest(TopDealsData data) {
        GreenKartHomePage homePage = new GreenKartHomePage(driver(), waitHelper());
        TopDealsPage topDealsPage = homePage.openTopDeals();
        topDealsPage.sortNameColumn();

        List<String> originalList = topDealsPage.getDisplayedNames();
        List<String> sortedList = originalList.stream()
                .sorted(String::compareToIgnoreCase)
                .toList();
        Assert.assertEquals(originalList, sortedList);

        String price = topDealsPage.getPriceFor(data.priceLookup());
        Assert.assertFalse(price.isBlank(), "Price for " + data.priceLookup() + " was not found");

        topDealsPage.closeAndReturn();
    }
}
