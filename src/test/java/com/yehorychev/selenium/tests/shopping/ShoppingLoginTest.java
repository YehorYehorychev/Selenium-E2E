package com.yehorychev.selenium.tests.shopping;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.core.BaseTest;
import com.yehorychev.selenium.pages.shopping.DashboardPage;
import com.yehorychev.selenium.pages.shopping.LoginPage;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ShoppingLoginTest extends BaseTest {

    @Override
    protected String getDefaultBaseUrlKey() {
        return "base.url.shopping";
    }

    @Test
    void loginViaUiFormTest() {
        LoginPage loginPage = new LoginPage(driver, waitHelper);
        DashboardPage dashboardPage = loginPage.login(ConfigProperties.getShoppingUsername(), ConfigProperties.getShoppingPassword());
        Assert.assertTrue(dashboardPage.isLoaded(), "Dashboard page should load after login");
        dashboardPage.signOut();
    }

    private String fetchAuthToken() {
        Map<String, String> payload = Map.of(
                "userEmail", ConfigProperties.getShoppingUsername(),
                "userPassword", ConfigProperties.getShoppingPassword()
        );

        return given()
                .baseUri(ConfigProperties.getShoppingApiBaseUrl())
                .basePath("/auth/login")
                .contentType(ContentType.JSON)
                .body(payload)
                .post()
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}

