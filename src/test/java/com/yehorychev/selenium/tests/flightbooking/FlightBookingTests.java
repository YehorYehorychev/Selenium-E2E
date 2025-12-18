package com.yehorychev.selenium.tests.flightbooking;

import com.yehorychev.selenium.data.FlightBookingDataProviders;
import com.yehorychev.selenium.data.FlightBookingTestData;
import com.yehorychev.selenium.pages.flightbooking.FlightBookingHomePage;
import com.yehorychev.selenium.core.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FlightBookingTests extends BaseTest {

    @Override
    protected String getDefaultBaseUrlKey() {
        return "base.url.flight.booking";
    }

    @Test(dataProvider = "flightBookingScenarios", dataProviderClass = FlightBookingDataProviders.class)
    void flightBookingTest(FlightBookingTestData data) {
        FlightBookingHomePage homePage = new FlightBookingHomePage(driver(), waitHelper());
        homePage.setCurrency(data.currency());
        String pageTitle = driver().getTitle();
        String pageUrl = driver().getCurrentUrl().split("\\?")[0];
        System.out.printf("The page title is: %s, and the page base URL is: %s", pageTitle, pageUrl);
        Assert.assertEquals(homePage.getSelectedCurrency(), data.currency());
    }

    @Test(dataProvider = "flightBookingScenarios", dataProviderClass = FlightBookingDataProviders.class)
    void flightBookingPassengersDropdownTest(FlightBookingTestData data) {
        FlightBookingHomePage homePage = new FlightBookingHomePage(driver(), waitHelper());
        homePage.openPassengerSelector();
        homePage.addAdults(data.adults());
        homePage.addChildren(data.children());
        homePage.confirmPassengers();
        String passengersInfo = homePage.getPassengerInfo();
        Assert.assertTrue(passengersInfo.contains(data.adults() + " Adult") && passengersInfo.contains(data.children() + " Child"),
                "Unexpected passengers info: " + passengersInfo);
    }

    @Test(dataProvider = "flightBookingScenarios", dataProviderClass = FlightBookingDataProviders.class)
    void flightBookingFromToCitiesTest(FlightBookingTestData data) {
        FlightBookingHomePage homePage = new FlightBookingHomePage(driver(), waitHelper());
        homePage.selectOrigin(data.origin());
        homePage.selectDestination(data.destination());
        Assert.assertTrue(homePage.getSelectedOrigin().contains(data.origin()));
        Assert.assertTrue(homePage.getSelectedDestination().contains(data.destination()));
    }

    @Test(dataProvider = "flightBookingScenarios", dataProviderClass = FlightBookingDataProviders.class)
    void flightBookingDynamicDropdownTest(FlightBookingTestData data) {
        FlightBookingHomePage homePage = new FlightBookingHomePage(driver(), waitHelper());
        homePage.searchCountry(data.countryQuery());
        homePage.chooseCountrySuggestion(data.countrySuggestion());
        Assert.assertEquals(homePage.getSelectedCountry(), data.countrySuggestion());
    }
}
