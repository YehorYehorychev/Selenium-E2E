package com.yehorychev.selenium.steps.amazon;

import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.pages.amazon.AmazonCartPage;
import com.yehorychev.selenium.pages.amazon.AmazonHomePage;
import com.yehorychev.selenium.pages.amazon.AmazonProductResultsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class AmazonSteps {

    private final ScenarioContext context;

    public AmazonSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the Amazon website is opened")
    public void theAmazonWebsiteIsOpened() {
        AmazonHomePage homePage = new AmazonHomePage(context.getDriver(), context.getWaitHelper());
        context.set("homePage", homePage);
    }

    @When("I search for {string}")
    public void iSearchFor(String keyword) {
        AmazonHomePage homePage = context.get("homePage");
        AmazonProductResultsPage resultsPage = homePage.searchFor(keyword);
        context.set("resultsPage", resultsPage);
        context.set("searchKeyword", keyword);
    }

    @Then("I should see search results")
    public void iShouldSeeSearchResults() {
        AmazonProductResultsPage resultsPage = context.get("resultsPage");
        Assert.assertFalse(resultsPage.getSearchResults().isEmpty(), "Search returned no results!");
    }

    @Then("the search box should contain {string}")
    public void theSearchBoxShouldContain(String keyword) {
        AmazonProductResultsPage resultsPage = context.get("resultsPage");
        Assert.assertEquals(resultsPage.getSearchBoxValue().toLowerCase(), keyword.toLowerCase());
    }

    @When("I hover over {string}")
    public void iHoverOver(String element) {
        AmazonHomePage homePage = context.get("homePage");
        if ("Accounts & Lists".equals(element)) {
            homePage.hoverOverAccountsAndLists();
        }
    }

    @When("I open the Watchlist")
    public void iOpenTheWatchlist() {
        AmazonHomePage homePage = context.get("homePage");
        homePage.openWatchlist();
    }

    @Then("I should see the Sign-In header")
    public void iShouldSeeTheSignInHeader() {
        AmazonHomePage homePage = context.get("homePage");
        Assert.assertTrue(homePage.isSignInHeaderDisplayed());
    }

    @When("I open the cart in a new window")
    public void iOpenTheCartInANewWindow() {
        AmazonHomePage homePage = context.get("homePage");
        AmazonCartPage cartPage = homePage.openCartInNewWindow();
        context.set("cartPage", cartPage);
    }

    @Then("the cart should be {string}")
    public void theCartShouldBe(String visibility) {
        AmazonCartPage cartPage = context.get("cartPage");
        boolean expectVisible = "visible".equalsIgnoreCase(visibility);

        if (expectVisible) {
            Assert.assertTrue(cartPage.isCartHeaderDisplayed());
        } else {
            Assert.assertFalse(cartPage.isCartHeaderDisplayed());
        }
    }
}

