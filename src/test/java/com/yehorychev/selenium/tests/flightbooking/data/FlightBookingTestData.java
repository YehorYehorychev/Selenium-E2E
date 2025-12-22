package com.yehorychev.selenium.tests.flightbooking.data;

public record FlightBookingTestData(String currency,
                                    int adults,
                                    int children,
                                    String origin,
                                    String destination,
                                    String countryQuery,
                                    String countrySuggestion) {
}
