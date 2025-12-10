package com.yehorychev.selenium;

import com.yehorychev.selenium.pages.flightbooking.FlightBookingHomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FlightBookingTests extends BaseTest {

    @Override
    protected String getDefaultBaseUrlKey() {
        return "base.url.flight.booking";
    }

    @Test
    void flightBookingTest() {
        FlightBookingHomePage homePage = new FlightBookingHomePage(driver, waitHelper);
        homePage.setCurrency("USD");
        String pageTitle = driver.getTitle();
        String pageUrl = driver.getCurrentUrl().split("\\?")[0];
        System.out.printf("The page title is: %s, and the page base URL is: %s", pageTitle, pageUrl);
        Assert.assertEquals(homePage.getSelectedCurrency(), "USD");
    }

    @Test
    void flightBookingPassengersDropdownTest() {
        FlightBookingHomePage homePage = new FlightBookingHomePage(driver, waitHelper);
        homePage.openPassengerSelector();
        homePage.addAdults(1);
        homePage.addChildren(2);
        homePage.confirmPassengers();
        String passengersInfo = homePage.getPassengerInfo();
        Assert.assertTrue(passengersInfo.contains("2 Adult") && passengersInfo.contains("2 Child"),
                "Expected '2 Adult, 2 Child' but got: " + passengersInfo);
    }

    @Test
    void flightBookingFromToCitiesTest() {
        FlightBookingHomePage homePage = new FlightBookingHomePage(driver, waitHelper);
        homePage.selectOrigin("GOI");
        homePage.selectDestination("BLR");
        Assert.assertEquals(homePage.getSelectedOrigin(), "Goa (GOI)");
        Assert.assertEquals(homePage.getSelectedDestination(), "Bengaluru (BLR)");
    }

    @Test
    void flightBookingDynamicDropdownTest() {
        FlightBookingHomePage homePage = new FlightBookingHomePage(driver, waitHelper);
        homePage.searchCountry("Br");
        homePage.chooseCountrySuggestion("Virgin Islands (British)");
        Assert.assertEquals(homePage.getSelectedCountry(), "Virgin Islands (British)");
    }
}
