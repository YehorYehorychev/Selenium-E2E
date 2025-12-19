package com.yehorychev.selenium.tests.greenkart;

import com.yehorychev.selenium.data.GreenKartDataProviders;
import com.yehorychev.selenium.data.GreenKartDataProviders.TopDealsData;
import com.yehorychev.selenium.data.GreenKartTestData;
import com.yehorychev.selenium.pages.greenkart.CartOverlay;
import com.yehorychev.selenium.pages.greenkart.CheckoutPage;
import com.yehorychev.selenium.pages.greenkart.GreenKartHomePage;
import com.yehorychev.selenium.pages.greenkart.TopDealsPage;
import com.yehorychev.selenium.core.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class GreenKartTests extends BaseTest {

    @Test(dataProvider = "greenKartCartScenarios", dataProviderClass = GreenKartDataProviders.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Add multiple vegetables to cart and apply promo code")
    void addItemToCartAndApplyPromoCodeTest(GreenKartTestData testData) {
        String[] veggiesToAdd = testData.vegetables().toArray(String[]::new);
        GreenKartHomePage homePage = openHomePage();
        addVegetables(homePage, veggiesToAdd);
        assertCartCount(homePage, veggiesToAdd.length);

        CartOverlay cartOverlay = openCartOverlay(homePage);
        CheckoutPage checkoutPage = proceedToCheckout(cartOverlay);

        verifyProductsInCart(checkoutPage, testData.expectedCartNames(), veggiesToAdd.length);
        applyPromoCode(checkoutPage, testData.promoCode());
    }

    @Test(dataProvider = "greenKartCartSorting", dataProviderClass = GreenKartDataProviders.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Ensure cart items are sorted alphabetically when expected")
    void verifyThatTheItemsSortedInCartTest(GreenKartTestData testData) {
        String[] veggiesToAdd = testData.vegetables().toArray(String[]::new);
        GreenKartHomePage homePage = openHomePage();
        addVegetables(homePage, veggiesToAdd);
        assertCartCount(homePage, veggiesToAdd.length);

        CheckoutPage checkoutPage = openCartOverlay(homePage)
                .proceedToCheckout();

        assertSortingIfNeeded(checkoutPage, testData);
    }

    @Test(dataProvider = "greenKartTopDeals", dataProviderClass = GreenKartDataProviders.class)
    @Severity(SeverityLevel.MINOR)
    @Description("Validate sorting and price lookup in Top Deals page")
    void verifyDiscountPricesTest(TopDealsData data) {
        GreenKartHomePage homePage = openHomePage();
        TopDealsPage topDealsPage = openTopDeals(homePage);
        sortNameColumn(topDealsPage);
        verifySortedNames(topDealsPage);
        verifyPriceFor(topDealsPage, data.priceLookup());
        closeTopDeals(topDealsPage);
    }

    @Step("Open GreenKart home page")
    private GreenKartHomePage openHomePage() {
        return Allure.step("Instantiate GreenKartHomePage", () -> new GreenKartHomePage(driver(), waitHelper()));
    }

    @Step("Add vegetables to cart")
    private void addVegetables(GreenKartHomePage homePage, String[] veggiesToAdd) {
        Allure.step("Add veggies to cart", () -> homePage.addVegetables(veggiesToAdd));
    }

    @Step("Verify cart count is {expectedCount}")
    private void assertCartCount(GreenKartHomePage homePage, int expectedCount) {
        Allure.step("Verify cart count", () -> Assert.assertTrue(homePage.cartContainsCount(expectedCount),
                "All selected items were not added to the cart."));
    }

    @Step("Open cart overlay")
    private CartOverlay openCartOverlay(GreenKartHomePage homePage) {
        return Allure.step("Open cart overlay", homePage::openCartOverlay);
    }

    @Step("Proceed to checkout")
    private CheckoutPage proceedToCheckout(CartOverlay cartOverlay) {
        return Allure.step("Proceed to checkout", cartOverlay::proceedToCheckout);
    }

    @Step("Verify products present in cart")
    private void verifyProductsInCart(CheckoutPage checkoutPage, List<String> expectedNames, int expectedSize) {
        Allure.step("Verify expected products in cart", () -> {
            List<String> productNames = checkoutPage.getProductNames(expectedSize);
            for (String expectedProductName : expectedNames) {
                Assert.assertTrue(productNames.contains(expectedProductName),
                        "Product name not found in cart: " + expectedProductName);
            }
        });
    }

    @Step("Apply promo code")
    private void applyPromoCode(CheckoutPage checkoutPage, String promoCode) {
        Allure.step("Apply promo code", () -> {
            checkoutPage.applyPromoCode(promoCode);
            Assert.assertEquals(checkoutPage.getPromoInfoText(), "Code applied ..!",
                    "Promo code was not applied successfully.");
        });
    }

    @Step("Assert sorting if required")
    private void assertSortingIfNeeded(CheckoutPage checkoutPage, GreenKartTestData testData) {
        Allure.step("Verify sorting of products", () -> {
            List<String> productNames = checkoutPage.getProductNames(testData.vegetables().size());
            List<String> sortedProductNames = productNames.stream()
                    .sorted(String::compareToIgnoreCase)
                    .toList();

            if (testData.expectSorted()) {
                Assert.assertEquals(productNames, sortedProductNames,
                        "Products in the cart are not sorted alphabetically");
            }
        });
    }

    @Step("Open Top Deals page")
    private TopDealsPage openTopDeals(GreenKartHomePage homePage) {
        return Allure.step("Open Top Deals page", homePage::openTopDeals);
    }

    @Step("Sort name column")
    private void sortNameColumn(TopDealsPage topDealsPage) {
        Allure.step("Sort Top Deals name column", topDealsPage::sortNameColumn);
    }

    @Step("Verify Top Deals names are sorted")
    private void verifySortedNames(TopDealsPage topDealsPage) {
        Allure.step("Verify sorting", () -> {
            List<String> originalList = topDealsPage.getDisplayedNames();
            List<String> sortedList = originalList.stream()
                    .sorted(String::compareToIgnoreCase)
                    .toList();
            Assert.assertEquals(originalList, sortedList);
        });
    }

    @Step("Verify price for {item}")
    private void verifyPriceFor(TopDealsPage topDealsPage, String item) {
        Allure.step("Verify price for item", () -> {
            String price = topDealsPage.getPriceFor(item);
            Assert.assertFalse(price.isBlank(), "Price for " + item + " was not found");
        });
    }

    @Step("Close Top Deals and return")
    private void closeTopDeals(TopDealsPage topDealsPage) {
        Allure.step("Close Top Deals", topDealsPage::closeAndReturn);
    }
}
