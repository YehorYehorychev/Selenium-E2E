package com.yehorychev.selenium.steps.flightbooking;

import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.pages.flightbooking.FlightBookingHomePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class FlightBookingSteps {

    private final ScenarioContext context;

    public FlightBookingSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the flight booking website is opened")
    public void theFlightBookingWebsiteIsOpened() {
        FlightBookingHomePage homePage = new FlightBookingHomePage(context.getDriver(), context.getWaitHelper());
        context.set("homePage", homePage);
    }

    @When("I select currency {string}")
    public void iSelectCurrency(String currency) {
        FlightBookingHomePage homePage = context.get("homePage");
        homePage.setCurrency(currency);
    }

    @Then("the selected currency should be {string}")
    public void theSelectedCurrencyShouldBe(String expectedCurrency) {
        FlightBookingHomePage homePage = context.get("homePage");
        Assert.assertEquals(homePage.getSelectedCurrency(), expectedCurrency);
    }

    @When("I open the passenger selector")
    public void iOpenThePassengerSelector() {
        FlightBookingHomePage homePage = context.get("homePage");
        homePage.openPassengerSelector();
    }

    @When("I add {string} adults")
    public void iAddAdults(String adults) {
        FlightBookingHomePage homePage = context.get("homePage");
        homePage.addAdults(Integer.parseInt(adults));
        context.set("adults", Integer.parseInt(adults));
    }

    @When("I add {string} children")
    public void iAddChildren(String children) {
        FlightBookingHomePage homePage = context.get("homePage");
        homePage.addChildren(Integer.parseInt(children));
        context.set("children", Integer.parseInt(children));
    }

    @When("I confirm passenger selection")
    public void iConfirmPassengerSelection() {
        FlightBookingHomePage homePage = context.get("homePage");
        homePage.confirmPassengers();
    }

    @Then("the passenger info should show {string} adults and {string} children")
    public void thePassengerInfoShouldShowAdultsAndChildren(String expectedAdults, String expectedChildren) {
        FlightBookingHomePage homePage = context.get("homePage");
        String passengersInfo = homePage.getPassengerInfo();

        Assert.assertTrue(passengersInfo.contains(expectedAdults + " Adult,") &&
                passengersInfo.contains(expectedChildren + " Child"),
                "Unexpected passengers info: " + passengersInfo);
    }

    @When("I select origin {string}")
    public void iSelectOrigin(String origin) {
        FlightBookingHomePage homePage = context.get("homePage");
        homePage.selectOrigin(origin);
    }

    @When("I select destination {string}")
    public void iSelectDestination(String destination) {
        FlightBookingHomePage homePage = context.get("homePage");
        homePage.selectDestination(destination);
    }

    @Then("the origin should be {string}")
    public void theOriginShouldBe(String expectedOrigin) {
        FlightBookingHomePage homePage = context.get("homePage");
        Assert.assertTrue(homePage.getSelectedOrigin().contains(expectedOrigin));
    }

    @Then("the destination should be {string}")
    public void theDestinationShouldBe(String expectedDestination) {
        FlightBookingHomePage homePage = context.get("homePage");
        Assert.assertTrue(homePage.getSelectedDestination().contains(expectedDestination));
    }

    @When("I search for country {string}")
    public void iSearchForCountry(String countryQuery) {
        FlightBookingHomePage homePage = context.get("homePage");
        homePage.searchCountry(countryQuery);
    }

    @When("I select country suggestion {string}")
    public void iSelectCountrySuggestion(String countrySuggestion) {
        FlightBookingHomePage homePage = context.get("homePage");
        homePage.chooseCountrySuggestion(countrySuggestion);
    }

    @Then("the selected country should be {string}")
    public void theSelectedCountryShouldBe(String expectedCountry) {
        FlightBookingHomePage homePage = context.get("homePage");
        Assert.assertEquals(homePage.getSelectedCountry(), expectedCountry);
    }
}

