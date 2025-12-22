package com.yehorychev.selenium.tests.shopping.ui;

import com.yehorychev.selenium.core.ShoppingAuthenticatedBaseTest;
import com.yehorychev.selenium.pages.shopping.DashboardPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ShoppingDashboardApiUiTest extends ShoppingAuthenticatedBaseTest {

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validate dashboard loads using API-authenticated session")
    public void dashboardLoadsWhenAuthenticatedViaApi() {
        DashboardPage dashboardPage = openDashboardStep();
        verifyDashboardLoaded(dashboardPage);
    }

    @Step("Open shopping dashboard")
    private DashboardPage openDashboardStep() {
        return Allure.step("Open dashboard page", this::openDashboard);
    }

    @Step("Verify dashboard page loaded")
    private void verifyDashboardLoaded(DashboardPage dashboardPage) {
        Allure.step("Verify dashboard page is loaded", () ->
                Assert.assertTrue(dashboardPage.isLoaded(), "Dashboard should load for authenticated session"));
    }
}
