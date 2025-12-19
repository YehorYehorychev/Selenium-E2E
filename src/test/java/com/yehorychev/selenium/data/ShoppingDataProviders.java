package com.yehorychev.selenium.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yehorychev.selenium.helpers.JsonDataHelper;
import org.testng.annotations.DataProvider;

import java.util.List;

public final class ShoppingDataProviders {

    private static final String SHOPPING_FIXTURE = "assets/data/shopping-products.json";

    private ShoppingDataProviders() {
    }

    @DataProvider(name = "shoppingProducts")
    public static Object[][] shoppingProducts() {
        List<ShoppingTestData> records = JsonDataHelper.readResource(
                SHOPPING_FIXTURE,
                new TypeReference<List<ShoppingTestData>>() {}
        );

        return records.stream()
                .map(record -> new Object[]{record})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "shoppingProductsFaker")
    public static Object[][] shoppingProductsFaker() {
        TestDataFactory.ShoppingCardDetails generated = TestDataFactory.randomShoppingCard();
        ShoppingTestData data = new ShoppingTestData(
                generated.productName(),
                generated.productPrice(),
                generated.countryQuery(),
                generated.countryToSelect()
        );
        return new Object[][]{{data}};
    }
}
