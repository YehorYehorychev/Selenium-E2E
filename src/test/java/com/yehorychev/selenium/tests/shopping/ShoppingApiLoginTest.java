package com.yehorychev.selenium.tests.shopping;

import com.yehorychev.selenium.config.ConfigProperties;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ShoppingApiLoginTest {

    @Test
    public void loginShouldReturnTokenAndMessage() {
        Map<String, String> payload = Map.of(
                "userEmail", ConfigProperties.getShoppingUsername(),
                "userPassword", ConfigProperties.getShoppingPassword()
        );

        ValidatableResponse body = given()
                .baseUri(ConfigProperties.getShoppingApiBaseUrl())
                .basePath("/auth/login")
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("message", equalTo("Login Successfully"));

        body.assertThat().body("token", notNullValue());
        String formattedBody = body.extract().body().asString().replace(",", ",\n");
        System.out.println("Response body:\n" + formattedBody);
    }
}

