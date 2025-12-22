package com.yehorychev.selenium.tests.greenkart.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yehorychev.selenium.helpers.JsonDataHelper;
import org.testng.annotations.DataProvider;

import java.util.List;

public final class GreenKartDataProviders {

    private GreenKartDataProviders() {
    }

    @DataProvider(name = "greenKartCartScenarios")
    public static Object[][] greenKartCartScenarios() {
        List<GreenKartTestData> records = JsonDataHelper.readResource(
                "assets/data/greenkart-products.json",
                new TypeReference<List<GreenKartTestData>>() {}
        );
        return records.stream()
                .map(data -> new Object[]{data})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "greenKartTopDeals")
    public static Object[][] greenKartTopDeals() {
        return JsonDataHelper.readResource(
                "assets/data/greenkart-top-deals.json",
                new TypeReference<List<TopDealsData>>() {}
        ).stream()
                .map(data -> new Object[]{data})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "greenKartCartSorting")
    public static Object[][] greenKartCartSorting() {
        return JsonDataHelper.readResource(
                "assets/data/greenkart-products.json",
                new TypeReference<List<GreenKartTestData>>() {}
        ).stream()
                .map(data -> new Object[]{data})
                .toArray(Object[][]::new);
    }

    public record TopDealsData(String sortedColumn, String priceLookup) {
    }
}
