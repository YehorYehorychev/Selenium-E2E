package com.yehorychev.selenium.tests.shared.data;

import net.datafaker.Faker;

import java.util.Locale;

/**
 * Central factory for random-but-realistic test data used across UI/API suites.
 */
public final class TestDataFactory {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private TestDataFactory() {
        // utility class
    }

    public static UserProfile randomUser() {
        return new UserProfile(
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                randomEmail(),
                FAKER.phoneNumber().cellPhone(),
                FAKER.address().streetAddress(),
                FAKER.address().city(),
                FAKER.address().state(),
                FAKER.address().zipCode(),
                FAKER.address().country()
        );
    }

    public static String randomEmail() {
        return FAKER.internet().emailAddress();
    }

    public static String randomFullName() {
        return FAKER.name().fullName();
    }

    public static ShoppingCardDetails randomShoppingCard() {
        return new ShoppingCardDetails(
                FAKER.commerce().productName(),
                "$" + FAKER.number().numberBetween(50, 15000),
                FAKER.finance().creditCard(),
                FAKER.number().digits(3),
                "12",
                "29",
                "United",
                "United States"
        );
    }

    /**
     * Simple immutable DTO for sharing generated user data across tests.
     */
    public record UserProfile(String firstName,
                              String lastName,
                              String email,
                              String phone,
                              String street,
                              String city,
                              String state,
                              String zip,
                              String country) {
    }

    public record ShoppingCardDetails(String productName,
                                      String productPrice,
                                      String cardNumber,
                                      String cvv,
                                      String expiryMonth,
                                      String expiryYear,
                                      String countryQuery,
                                      String countryToSelect) {
    }
}
