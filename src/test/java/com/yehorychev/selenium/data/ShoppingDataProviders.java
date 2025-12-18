package com.yehorychev.selenium.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yehorychev.selenium.config.ConfigProperties;
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
                .map(ShoppingDataProviders::resolveSecrets)
                .map(record -> new Object[]{record})
                .toArray(Object[][]::new);
    }

    private static ShoppingTestData resolveSecrets(ShoppingTestData record) {
        String card = substitute(record.cardNumber(), ConfigProperties.getShoppingCardNumber());
        String cvv = substitute(record.cvv(), ConfigProperties.getShoppingCardCvv());
        return record.withCardDetails(card, cvv);
    }

    private static String substitute(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value
                .replace("${shopping.card.number}", ConfigProperties.getShoppingCardNumber())
                .replace("${shopping.card.cvv}", ConfigProperties.getShoppingCardCvv());
    }
}
