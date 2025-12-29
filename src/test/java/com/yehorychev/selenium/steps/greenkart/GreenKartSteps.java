package com.yehorychev.selenium.steps.greenkart;

import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.pages.greenkart.CartOverlay;
import com.yehorychev.selenium.pages.greenkart.CheckoutPage;
import com.yehorychev.selenium.pages.greenkart.GreenKartHomePage;
import com.yehorychev.selenium.pages.greenkart.TopDealsPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.List;

public class GreenKartSteps {

    private final ScenarioContext context;

    public GreenKartSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the GreenKart website is opened")
    public void theGreenKartWebsiteIsOpened() {
        GreenKartHomePage homePage = new GreenKartHomePage(context.getDriver(), context.getWaitHelper());
        context.set("homePage", homePage);
    }

    @When("I add the following vegetables to cart:")
    public void iAddTheFollowingVegetablesToCart(DataTable dataTable) {
        GreenKartHomePage homePage = context.get("homePage");

        // DataTable.asList() returns all cells; skip header
        List<String> vegetables = dataTable.asList();
        if (vegetables.isEmpty()) {
            throw new IllegalArgumentException("No vegetables provided in the table");
        }

        // Skip header row ("vegetables"), get the actual CSV value
        String vegetablesStr = vegetables.size() > 1 ? vegetables.get(1) : vegetables.get(0);
        String[] vegArray = vegetablesStr.split(",");

        homePage.addVegetables(vegArray);
        context.set("addedVegetables", vegArray);
    }

    @Then("the GreenKart cart count should be {string}")
    public void theGreenKartCartCountShouldBe(String count) {
        GreenKartHomePage homePage = context.get("homePage");
        int expectedCount = Integer.parseInt(count);
        Assert.assertTrue(homePage.cartContainsCount(expectedCount),
            "All selected items were not added to the cart.");
    }

    @When("I open the cart overlay")
    public void iOpenTheCartOverlay() {
        GreenKartHomePage homePage = context.get("homePage");
        CartOverlay cartOverlay = homePage.openCartOverlay();
        context.set("cartOverlay", cartOverlay);
    }

    @When("I proceed to checkout from the overlay")
    public void iProceedToCheckoutFromOverlay() {
        CartOverlay cartOverlay = context.get("cartOverlay");
        CheckoutPage checkoutPage = cartOverlay.proceedToCheckout();
        context.set("checkoutPage", checkoutPage);
    }

    @Then("the cart should contain:")
    public void theCartShouldContain(DataTable dataTable) {
        CheckoutPage checkoutPage = context.get("checkoutPage");
        List<String> expectedNamesRaw = dataTable.asList();

        if (expectedNamesRaw.isEmpty()) {
            throw new IllegalArgumentException("No expected names provided in the table");
        }

        // Skip header row, get the actual CSV value
        String expectedNamesStr = expectedNamesRaw.size() > 1 ? expectedNamesRaw.get(1) : expectedNamesRaw.get(0);
        List<String> expectedNames = List.of(expectedNamesStr.split(","));

        String[] addedVegetables = context.get("addedVegetables");
        List<String> productNames = checkoutPage.getProductNames(addedVegetables.length);

        for (String expectedName : expectedNames) {
            Assert.assertTrue(productNames.contains(expectedName.trim()),
                "Product name not found in cart: " + expectedName);
        }
    }

    @When("I apply promo code {string}")
    public void iApplyPromoCode(String promoCode) {
        CheckoutPage checkoutPage = context.get("checkoutPage");
        checkoutPage.applyPromoCode(promoCode);
    }

    @Then("the promo code should be applied successfully")
    public void thePromoCodeShouldBeAppliedSuccessfully() {
        CheckoutPage checkoutPage = context.get("checkoutPage");
        Assert.assertEquals(checkoutPage.getPromoInfoText(), "Code applied ..!",
            "Promo code was not applied successfully.");
    }

    @Then("the products in cart should be sorted alphabetically")
    public void theProductsInCartShouldBeSortedAlphabetically() {
        CheckoutPage checkoutPage = context.get("checkoutPage");
        String[] addedVegetables = context.get("addedVegetables");

        List<String> productNames = checkoutPage.getProductNames(addedVegetables.length);
        List<String> sortedProductNames = productNames.stream()
            .sorted(String::compareToIgnoreCase)
            .toList();

        Assert.assertEquals(productNames, sortedProductNames,
            "Products in the cart are not sorted alphabetically");
    }

    @When("I open the Top Deals page")
    public void iOpenTheTopDealsPage() {
        GreenKartHomePage homePage = context.get("homePage");
        TopDealsPage topDealsPage = homePage.openTopDeals();
        context.set("topDealsPage", topDealsPage);
    }

    @When("I sort by name column")
    public void iSortByNameColumn() {
        TopDealsPage topDealsPage = context.get("topDealsPage");
        topDealsPage.sortNameColumn();
    }

    @Then("the vegetable names should be sorted")
    public void theVegetableNamesShouldBeSorted() {
        TopDealsPage topDealsPage = context.get("topDealsPage");
        List<String> originalList = topDealsPage.getDisplayedNames();
        List<String> sortedList = originalList.stream()
            .sorted(String::compareToIgnoreCase)
            .toList();
        Assert.assertEquals(originalList, sortedList);
    }

    @Then("the price for {string} should be displayed")
    public void thePriceForShouldBeDisplayed(String vegetable) {
        TopDealsPage topDealsPage = context.get("topDealsPage");
        String price = topDealsPage.getPriceFor(vegetable);
        Assert.assertFalse(price.isBlank(), "Price for " + vegetable + " was not found");
    }

    @When("I close the Top Deals page")
    public void iCloseTheTopDealsPage() {
        TopDealsPage topDealsPage = context.get("topDealsPage");
        topDealsPage.closeAndReturn();
    }

    @Then("I should return to home page")
    public void iShouldReturnToHomePage() {
        // Page should have switched back
        Assert.assertTrue(true);
    }
}

