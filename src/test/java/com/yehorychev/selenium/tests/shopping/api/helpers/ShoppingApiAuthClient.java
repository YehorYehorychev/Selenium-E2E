package com.yehorychev.selenium.tests.shopping.api.helpers;

import com.yehorychev.selenium.config.ConfigProperties;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Thin wrapper over shopping authentication endpoints to simplify pre-auth in tests.
 */
public class ShoppingApiAuthClient {

    private static final Logger log = LoggerFactory.getLogger(ShoppingApiAuthClient.class);

    public ShoppingSession loginWithConfiguredUser() {
        return login(ConfigProperties.getShoppingUsername(), ConfigProperties.getShoppingPassword());
    }

    public ShoppingSession login(String email, String password) {
        log.info("Requesting shopping token via API for user {}", email);
        Map<String, String> payload = Map.of(
                "userEmail", email,
                "userPassword", password
        );

        Response response = given()
                .baseUri(ConfigProperties.getShoppingApiBaseUrl())
                .basePath("/auth/login")
                .contentType(ContentType.JSON)
                .body(payload)
                .post()
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = response.path("token");
        String userId = response.path("userId");
        String message = response.path("message");
        return new ShoppingSession(token, userId, email, message);
    }
}
