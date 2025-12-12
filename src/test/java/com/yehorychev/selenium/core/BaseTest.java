package com.yehorychev.selenium.core;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.helpers.ScreenshotHelper;
import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<WaitHelper> WAIT_HELPER = new ThreadLocal<>();

    protected WebDriver driver() {
        WebDriver current = DRIVER.get();
        if (current == null) {
            throw new IllegalStateException("WebDriver is not initialized for current thread");
        }
        return current;
    }

    protected WaitHelper waitHelper() {
        WaitHelper helper = WAIT_HELPER.get();
        if (helper == null) {
            throw new IllegalStateException("WaitHelper is not initialized for current thread");
        }
        return helper;
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"baseUrlKey"})
    public void setUp(@Optional("") String baseUrlKey) {
        ChromeOptions options = buildChromeOptions();

        WebDriver webDriver = new ChromeDriver(options);
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        WaitHelper helper = WaitHelper.fromConfig(webDriver);
        DRIVER.set(webDriver);
        WAIT_HELPER.set(helper);

        webDriver.navigate().to(resolveBaseUrl(baseUrlKey));
    }

    private String resolveBaseUrl(String overrideKey) {
        String keyToUse = (overrideKey != null && !overrideKey.isBlank()) ? overrideKey : getDefaultBaseUrlKey();
        return ConfigProperties.getProperty(keyToUse);
    }

    protected String getDefaultBaseUrlKey() {
        return "base.url.green.kart";
    }

    protected ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        return options;
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        WebDriver webDriver = DRIVER.get();
        try {
            if (webDriver != null) {
                boolean failuresOnly = ConfigProperties.captureScreenshotsOnFailuresOnly();
                boolean shouldCapture = !failuresOnly || result.getStatus() == ITestResult.FAILURE;
                if (shouldCapture) {
                    ScreenshotHelper.capture(webDriver, result.getMethod().getMethodName());
                }
            }
        } catch (RuntimeException screenshotError) {
            // screenshot failures should not block driver cleanup
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
            DRIVER.remove();
            WAIT_HELPER.remove();
        }
    }
}
