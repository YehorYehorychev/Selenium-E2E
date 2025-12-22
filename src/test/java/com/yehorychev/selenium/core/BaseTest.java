package com.yehorychev.selenium.core;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.helpers.ScreenshotHelper;
import com.yehorychev.selenium.helpers.WaitHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.SeverityLevel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BaseTest {

    // Thread-local storage for WebDriver and WaitHelper instances,
    // ensuring thread safety during parallel test execution.
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<WaitHelper> WAIT_HELPER = new ThreadLocal<>();
    private static final ReentrantLock SAFARI_LOCK = new ReentrantLock();
    private static final ThreadLocal<Boolean> SAFARI_LOCK_HELD = ThreadLocal.withInitial(() -> false);

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
    @Parameters({"baseUrlKey", "browser"})
    public void setUp(@Optional("") String baseUrlKey, @Optional("") String browserParam) {
        BrowserType browserType = resolveBrowserType(browserParam);
        lockSafariIfNeeded(browserType);
        WebDriver webDriver = null;
        try {
            webDriver = createWebDriver(browserType);
            webDriver.manage().window().maximize();
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

            WaitHelper helper = WaitHelper.fromConfig(webDriver);
            DRIVER.set(webDriver);
            WAIT_HELPER.set(helper);
            setTestDriverAttribute(webDriver);

            webDriver.navigate().to(resolveBaseUrl(baseUrlKey));
        } catch (RuntimeException e) {
            if (webDriver != null) {
                webDriver.quit();
            }
            releaseSafariLockIfHeld();
            throw e;
        }
    }

    private void lockSafariIfNeeded(BrowserType browserType) {
        if (browserType == BrowserType.SAFARI) {
            SAFARI_LOCK.lock();
            SAFARI_LOCK_HELD.set(true);
        }
    }

    private BrowserType resolveBrowserType(String browserParam) {
        String selection = firstNonBlank(browserParam, ConfigProperties.getBrowserOverride(), ConfigProperties.getDefaultBrowser());
        return BrowserType.from(selection);
    }

    private WebDriver createWebDriver(BrowserType browserType) {
        return switch (browserType) {
            case FIREFOX -> {
                WebDriverManager.firefoxdriver().setup();
                yield new FirefoxDriver(buildFirefoxOptions());
            }
            case SAFARI -> new SafariDriver(buildSafariOptions());
            case CHROME -> {
                WebDriverManager.chromedriver().setup();
                yield new ChromeDriver(buildChromeOptions());
            }
        };
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
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
        if (ConfigProperties.isHeadlessEnabled()) {
            ConfigProperties.getHeadlessArguments().forEach(options::addArguments);
        }
        return options;
    }

    protected FirefoxOptions buildFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-private");
        if (ConfigProperties.isHeadlessEnabled()) {
            options.addArguments("-headless");
        }
        return options;
    }

    protected SafariOptions buildSafariOptions() {
        SafariOptions options = new SafariOptions();
        options.setAutomaticInspection(false);
        options.setAutomaticProfiling(false);
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
                    Path screenshotPath = ScreenshotHelper.capture(webDriver, result.getMethod().getMethodName());
                    attachFileToAllure(screenshotPath, "image/png");
                }
                if (result.getStatus() == ITestResult.FAILURE) {
                    Path pageSource = ScreenshotHelper.capturePageSource(webDriver, result.getMethod().getMethodName());
                    attachFileToAllure(pageSource, "text/html");
                    attachConsoleLogs(webDriver);
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
            result.removeAttribute("driver");
            releaseSafariLockIfHeld();
        }
    }

    protected void annotateDataSet(String description, SeverityLevel severity) {
        Allure.description(description);
        Allure.getLifecycle().updateTestCase(testResult -> testResult.setDescription(description));
        Allure.label("severity", severity.value());
    }

    private void attachFileToAllure(Path path, String mimeType) {
        try (var input = Files.newInputStream(path)) {
            Allure.addAttachment(path.getFileName().toString(), mimeType, input, path.toString().endsWith(".html") ? "html" : "png");
        } catch (IOException ignored) {
            // ignore attachment failures
        }
    }

    private void attachConsoleLogs(WebDriver webDriver) {
        try {
            var logs = webDriver.manage().logs().get("browser");
            StringBuilder builder = new StringBuilder();
            logs.forEach(entry -> builder.append(entry.getLevel()).append(": ").append(entry.getMessage()).append(System.lineSeparator()));
            Allure.addAttachment("Browser console", builder.toString());
        } catch (Exception ignored) {
            // some drivers may not support log retrieval
        }
    }

    private void releaseSafariLockIfHeld() {
        if (Boolean.TRUE.equals(SAFARI_LOCK_HELD.get())) {
            SAFARI_LOCK_HELD.set(false);
            SAFARI_LOCK.unlock();
        }
    }

    private void setTestDriverAttribute(WebDriver driver) {
        ITestResult currentResult = Reporter.getCurrentTestResult();
        if (currentResult != null) {
            currentResult.setAttribute("driver", driver);
        }
    }

    protected enum BrowserType {
        CHROME,
        FIREFOX,
        SAFARI;

        static BrowserType from(String value) {
            if (value == null || value.isBlank()) {
                return CHROME;
            }
            try {
                return BrowserType.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported browser: " + value + ". Supported: " + java.util.Arrays.toString(values()), ex);
            }
        }
    }
}
