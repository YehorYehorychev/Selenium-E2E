package com.yehorychev.selenium.tests.amazon.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yehorychev.selenium.helpers.JsonDataHelper;
import org.testng.annotations.DataProvider;

import java.util.List;

public final class AmazonDataProviders {

    private AmazonDataProviders() {
    }

    @DataProvider(name = "amazonSearchKeywords")
    public static Object[][] amazonSearchKeywords() {
        List<String> keywords = JsonDataHelper.readResource(
                "assets/data/amazon-search-keywords.json",
                new TypeReference<List<String>>() {}
        );
        return keywords.stream()
                .map(keyword -> new Object[]{keyword})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "amazonCartAccess")
    public static Object[][] amazonCartAccess() {
        return new Object[][]{{true}};
    }
}
