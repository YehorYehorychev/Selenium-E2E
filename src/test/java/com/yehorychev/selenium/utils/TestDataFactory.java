package com.yehorychev.selenium.utils;

import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Factory for generating random test data using JavaFaker.
 */
public class TestDataFactory {
    private static final Faker faker = new Faker();

    /**
     * Generate a random user profile.
     *
     * @return UserProfile with random data
     */
    public static UserProfile randomUser() {
        return new UserProfile(
                faker.name().fullName(),
                faker.internet().emailAddress(),
                faker.phoneNumber().subscriberNumber(10),
                faker.address().streetAddress(),
                faker.address().city(),
                faker.address().state(),
                faker.address().zipCode(),
                faker.address().country()
        );
    }

    /**
     * Generate a random credit card.
     *
     * @return CreditCard with random data
     */
    public static CreditCard randomCreditCard() {
        LocalDate expiry = LocalDate.now().plusYears(2).plusMonths(faker.number().numberBetween(1, 12));
        return new CreditCard(
                faker.business().creditCardNumber().replaceAll("-", ""),
                faker.number().digits(3),
                expiry.format(DateTimeFormatter.ofPattern("MM/yy"))
        );
    }

    /**
     * Generate random shopping card details (for shopping site checkout).
     *
     * @return ShoppingCardDetails with random card data
     */
    public static ShoppingCardDetails randomShoppingCard() {
        // Use fixed year 2029 since the website dropdown may have limited options
        int expiryMonth = faker.number().numberBetween(1, 13);
        return new ShoppingCardDetails(
                faker.business().creditCardNumber().replaceAll("-", ""),
                faker.number().digits(3),
                String.format("%02d", expiryMonth),
                "2029"
        );
    }

    /**
     * Generate a random full name.
     *
     * @return Random full name
     */
    public static String randomFullName() {
        return faker.name().fullName();
    }

    /**
     * User profile data.
     */
    public record UserProfile(
            String fullName,
            String email,
            String phone,
            String address,
            String city,
            String state,
            String zipCode,
            String country
    ) {
    }

    /**
     * Credit card data.
     */
    public record CreditCard(
            String number,
            String cvv,
            String expiry
    ) {
    }

    /**
     * Shopping card details (month/year separate for shopping site).
     */
    public record ShoppingCardDetails(
            String cardNumber,
            String cvv,
            String expiryMonth,
            String expiryYear
    ) {
    }
}

