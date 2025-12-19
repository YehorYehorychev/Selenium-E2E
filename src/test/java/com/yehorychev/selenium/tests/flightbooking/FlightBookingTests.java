package com.yehorychev.selenium.tests.flightbooking;

import com.yehorychev.selenium.data.FlightBookingDataProviders;
import com.yehorychev.selenium.data.FlightBookingTestData;
import com.yehorychev.selenium.pages.flightbooking.FlightBookingHomePage;
import com.yehorychev.selenium.core.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FlightBookingTests extends BaseTest {

    @Override
    protected String getDefaultBaseUrlKey() {
        return "base.url.flight.booking";
    }

    @Test(dataProvider = "flightBookingScenarios", dataProviderClass = FlightBookingDataProviders.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify currency selector applies user choice")
    void flightBookingTest(FlightBookingTestData data) {
        FlightBookingHomePage homePage = openHomePage();
        selectCurrency(homePage, data.currency());
        logPageMetadata();
        assertCurrencySelected(homePage, data.currency());
    }

    @Test(dataProvider = "flightBookingScenarios", dataProviderClass = FlightBookingDataProviders.class)
    @Severity(SeverityLevel.CRITICAL)
    void flightBookingPassengersDropdownTest(FlightBookingTestData data) {
        FlightBookingHomePage homePage = openHomePage();
        configurePassengers(homePage, data);
        verifyPassengerComposition(homePage, data);
    }

    @Test(dataProvider = "flightBookingScenarios", dataProviderClass = FlightBookingDataProviders.class)
    @Severity(SeverityLevel.NORMAL)
    void flightBookingFromToCitiesTest(FlightBookingTestData data) {
        FlightBookingHomePage homePage = openHomePage();
        selectRoute(homePage, data);
        verifyRouteSelection(homePage, data);
    }

    @Test(dataProvider = "flightBookingScenarios", dataProviderClass = FlightBookingDataProviders.class)
    @Severity(SeverityLevel.MINOR)
    void flightBookingDynamicDropdownTest(FlightBookingTestData data) {
        FlightBookingHomePage homePage = openHomePage();
        searchCountry(homePage, data);
        verifyCountrySelection(homePage, data);
    }

    @Step("Open flight booking home page")
    private FlightBookingHomePage openHomePage() {
        return Allure.step("Instantiate FlightBookingHomePage", () -> new FlightBookingHomePage(driver(), waitHelper()));
    }

    @Step("Select currency: {currency}")
    private void selectCurrency(FlightBookingHomePage homePage, String currency) {
        Allure.step("Select currency " + currency, () -> homePage.setCurrency(currency));
    }

    @Step("Assert currency {expected} is selected")
    private void assertCurrencySelected(FlightBookingHomePage homePage, String expected) {
        Allure.step("Verify selected currency", () -> Assert.assertEquals(homePage.getSelectedCurrency(), expected));
    }

    @Step("Log page metadata")
    private void logPageMetadata() {
        Allure.step("Log title and URL", () -> {
            String pageTitle = driver().getTitle();
            String pageUrl = driver().getCurrentUrl().split("\\?")[0];
            Allure.addAttachment("Flight page metadata", String.format("Title: %s%nURL: %s", pageTitle, pageUrl));
        });
    }

    @Step("Configure passengers")
    private void configurePassengers(FlightBookingHomePage homePage, FlightBookingTestData data) {
        Allure.step("Configure passenger counts", () -> {
            homePage.openPassengerSelector();
            homePage.addAdults(data.adults());
            homePage.addChildren(data.children());
            homePage.confirmPassengers();
        });
    }

    @Step("Verify passengers composition")
    private void verifyPassengerComposition(FlightBookingHomePage homePage, FlightBookingTestData data) {
        Allure.step("Verify passengers info", () -> {
            String passengersInfo = homePage.getPassengerInfo();
            Assert.assertTrue(passengersInfo.contains(data.adults() + " Adult,") &&
                    passengersInfo.contains(data.children() + " Child"),
                    "Unexpected passengers info: " + passengersInfo);
        });
    }

    @Step("Select route origin/destination")
    private void selectRoute(FlightBookingHomePage homePage, FlightBookingTestData data) {
        Allure.step("Select origin and destination", () -> {
            homePage.selectOrigin(data.origin());
            homePage.selectDestination(data.destination());
        });
    }

    @Step("Verify route selection")
    private void verifyRouteSelection(FlightBookingHomePage homePage, FlightBookingTestData data) {
        Allure.step("Assert route selection", () -> {
            Assert.assertTrue(homePage.getSelectedOrigin().contains(data.origin()));
            Assert.assertTrue(homePage.getSelectedDestination().contains(data.destination()));
        });
    }

    @Step("Search and pick country")
    private void searchCountry(FlightBookingHomePage homePage, FlightBookingTestData data) {
        Allure.step("Search country", () -> {
            homePage.searchCountry(data.countryQuery());
            homePage.chooseCountrySuggestion(data.countrySuggestion());
        });
    }

    @Step("Verify country selection")
    private void verifyCountrySelection(FlightBookingHomePage homePage, FlightBookingTestData data) {
        Allure.step("Verify country selection", () ->
                Assert.assertEquals(homePage.getSelectedCountry(), data.countrySuggestion()));
    }
}
