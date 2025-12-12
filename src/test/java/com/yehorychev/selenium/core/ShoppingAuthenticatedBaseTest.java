package com.yehorychev.selenium.core;

import com.yehorychev.selenium.api.ShoppingApiAuthClient;
import com.yehorychev.selenium.api.ShoppingSession;
import com.yehorychev.selenium.pages.shopping.DashboardPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Base test to preload shopping session via API and start UI scenarios already authenticated.
 */
public abstract class ShoppingAuthenticatedBaseTest extends BaseTest {

    private ShoppingSession shoppingSession;

    @BeforeClass(alwaysRun = true)
    public void obtainShoppingSession() {
        shoppingSession = new ShoppingApiAuthClient().loginWithConfiguredUser();
    }

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp(String baseUrlKey) {
        super.setUp(getDefaultBaseUrlKey());
        injectShoppingToken();
    }

    private void injectShoppingToken() {
        if (!(driver instanceof JavascriptExecutor executor)) {
            throw new IllegalStateException("Driver must support JS to inject token");
        }
        executor.executeScript(
                "window.localStorage.setItem(arguments[0], arguments[1]);",
                "token",
                shoppingSession.token()
        );
        driver.navigate().refresh();
        waitHelper.waitForPageReady();
    }

    @Override
    protected String getDefaultBaseUrlKey() {
        return "base.url.shopping";
    }

    protected DashboardPage openDashboard() {
        return new DashboardPage(driver, waitHelper);
    }
}

