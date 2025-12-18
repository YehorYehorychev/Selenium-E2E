package com.yehorychev.selenium.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yehorychev.selenium.helpers.JsonDataHelper;
import org.testng.annotations.DataProvider;

import java.util.List;

public final class FlightBookingDataProviders {

    private FlightBookingDataProviders() {
    }

    @DataProvider(name = "flightBookingScenarios")
    public static Object[][] flightBookingScenarios() {
        List<FlightBookingTestData> records = JsonDataHelper.readResource(
                "assets/data/flightbooking-passengers.json",
                new TypeReference<List<FlightBookingTestData>>() {}
        );
        return records.stream()
                .map(data -> new Object[]{data})
                .toArray(Object[][]::new);
    }
}

