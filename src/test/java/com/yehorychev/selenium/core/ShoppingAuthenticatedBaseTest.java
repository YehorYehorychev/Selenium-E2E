package com.yehorychev.selenium.core;

import com.yehorychev.selenium.api.ShoppingApiAuthClient;
import com.yehorychev.selenium.api.ShoppingSession;
import com.yehorychev.selenium.pages.shopping.DashboardPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Base test to preload shopping session via API and start UI scenarios already authenticated.
 */
public abstract class ShoppingAuthenticatedBaseTest extends BaseTest {

    private ShoppingSession shoppingSession;

    @BeforeClass(alwaysRun = true)
    public void obtainShoppingSession() {
        shoppingSession = requestSession();
    }

    @BeforeMethod(alwaysRun = true)
    @Override
    @Parameters({"baseUrlKey"})
    public void setUp(@Optional("") String baseUrlKey) {
        super.setUp(baseUrlKey);
        if (shoppingSession == null) {
            shoppingSession = requestSession();
        }
        injectShoppingToken();
    }

    private ShoppingSession requestSession() {
        return new ShoppingApiAuthClient().loginWithConfiguredUser();
    }

    private void injectShoppingToken() {
        if (!(driver instanceof JavascriptExecutor executor)) {
            throw new IllegalStateException("Driver must support JS to inject token");
        }
        executor.executeScript("window.localStorage.setItem(arguments[0], arguments[1]);", "token", shoppingSession.token());
        executor.executeScript("window.localStorage.setItem(arguments[0], arguments[1]);", "userEmail", shoppingSession.userEmail());
        executor.executeScript("window.localStorage.setItem(arguments[0], arguments[1]);", "userId", shoppingSession.userId());
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

    protected ShoppingSession getShoppingSession() {
        return shoppingSession;
    }
}
