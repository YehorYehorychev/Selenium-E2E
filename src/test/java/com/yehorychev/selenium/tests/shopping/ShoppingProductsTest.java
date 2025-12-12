package com.yehorychev.selenium.tests.shopping;

import com.yehorychev.selenium.core.ShoppingAuthenticatedBaseTest;
import com.yehorychev.selenium.pages.shopping.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ShoppingProductsTest extends ShoppingAuthenticatedBaseTest {

    @Test
    public void shoppingProductsTest() {
        DashboardPage dashboardPage = openDashboard();
        Assert.assertTrue(dashboardPage.isLoaded(), "Dashboard page is not loaded properly");
        Assert.assertTrue(dashboardPage.containsProduct("ZARA COAT 3"), "ZARA COAT 3 item should be present in products list");
    }
}
