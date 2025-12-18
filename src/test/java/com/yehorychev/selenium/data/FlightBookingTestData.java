package com.yehorychev.selenium.data;

public record FlightBookingTestData(String currency,
                                    int adults,
                                    int children,
                                    String origin,
                                    String destination,
                                    String countryQuery,
                                    String countrySuggestion) {
}

