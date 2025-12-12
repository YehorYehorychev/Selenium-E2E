package com.yehorychev.selenium.tests.shopping;

import com.yehorychev.selenium.core.ShoppingAuthenticatedBaseTest;
import com.yehorychev.selenium.pages.shopping.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ShoppingDashboardApiUiTest extends ShoppingAuthenticatedBaseTest {

    @Test
    public void dashboardLoadsWhenAuthenticatedViaApi() {
        DashboardPage dashboardPage = openDashboard();
        Assert.assertTrue(dashboardPage.isLoaded(), "Dashboard should load for authenticated session");
    }
}

