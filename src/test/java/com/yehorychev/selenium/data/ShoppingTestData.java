package com.yehorychev.selenium.data;

public record ShoppingTestData(String productName,
                               String productPrice,
                               String cardNumber,
                               String cvv,
                               String cardHolderName,
                               String expiryMonth,
                               String expiryYear,
                               String countryQuery,
                               String countryToSelect) {

    public static ShoppingTestData defaultData(String cardNumber, String cvv) {
        return new ShoppingTestData(
                "ZARA COAT 3",
                "$ 11500",
                cardNumber,
                cvv,
                "Yehor Test",
                "12",
                "29",
                "United",
                "United States"
        );
    }

    public ShoppingTestData withCardDetails(String newCardNumber, String newCvv) {
        return new ShoppingTestData(
                productName,
                productPrice,
                newCardNumber,
                newCvv,
                cardHolderName,
                expiryMonth,
                expiryYear,
                countryQuery,
                countryToSelect
        );
    }
}
