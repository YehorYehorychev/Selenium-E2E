package com.yehorychev.selenium.hooks;

import com.yehorychev.selenium.config.ConfigProperties;
import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.helpers.ScreenshotHelper;
import com.yehorychev.selenium.helpers.WaitHelper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

/**
 * Cucumber Hooks for setup and teardown of WebDriver per scenario.
 * Manages WebDriver lifecycle and screenshot capture on failure.
 */
public class CucumberHooks {

    private static final Logger logger = LoggerFactory.getLogger(CucumberHooks.class);
    private final ScenarioContext context;

    public CucumberHooks(ScenarioContext context) {
        this.context = context;
    }

    @Before(order = 0)
    public void beforeScenario(Scenario scenario) {
        logger.info("========================================");
        logger.info("Starting Scenario: {}", scenario.getName());
        logger.info("Tags: {}", scenario.getSourceTagNames());
        logger.info("========================================");

        context.setScenarioName(scenario.getName());

        // Initialize WebDriver
        String browser = ConfigProperties.getBrowser();
        boolean headless = ConfigProperties.isHeadless();
        String gridUrl = ConfigProperties.getSeleniumGridUrl();

        WebDriver driver = createWebDriver(browser, headless, gridUrl);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        context.setDriver(driver);

        // Initialize WaitHelper
        WaitHelper waitHelper = WaitHelper.fromConfig(driver);
        context.setWaitHelper(waitHelper);

        logger.info("WebDriver initialized: {} (headless: {})", browser, headless);

        // Navigate to base URL based on scenario tags
        String baseUrl = determineBaseUrl(scenario);
        if (baseUrl != null && !baseUrl.isEmpty()) {
            driver.get(baseUrl);
            logger.info("Navigated to base URL: {}", baseUrl);
        }
    }

    @After(order = 0)
    public void afterScenario(Scenario scenario) {
        WebDriver driver = context.getDriver();

        if (driver != null) {
            // Capture screenshot on failure
            if (scenario.isFailed()) {
                logger.error("❌ Scenario FAILED: {}", scenario.getName());
                captureScreenshot(scenario, driver);
            } else {
                logger.info("✅ Scenario PASSED: {}", scenario.getName());
            }

            // Always capture screenshot if configured
            boolean failuresOnly = ConfigProperties.captureScreenshotsOnFailuresOnly();
            if (!failuresOnly || scenario.isFailed()) {
                captureScreenshotForAllure(driver, scenario.getName());
            }

            // Quit driver
            try {
                driver.quit();
                logger.info("WebDriver quit successfully");
            } catch (Exception e) {
                logger.warn("Error quitting WebDriver: {}", e.getMessage());
            }
        }

        context.clear();
        logger.info("========================================");
        logger.info("Scenario completed: {}", scenario.getName());
        logger.info("========================================\n");
    }

    /**
     * Attach screenshot to Cucumber report
     */
    private void captureScreenshot(Scenario scenario, WebDriver driver) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName() + "_failure");
            logger.info("Screenshot attached to Cucumber report");
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
        }
    }

    /**
     * Attach screenshot to Allure report
     */
    private void captureScreenshotForAllure(WebDriver driver, String scenarioName) {
        try {
            ScreenshotHelper.capture(driver, scenarioName);

            // Also attach to Allure
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot: " + scenarioName,
                                new ByteArrayInputStream(screenshot));
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for Allure: {}", e.getMessage());
        }
    }

    /**
     * Create WebDriver instance based on browser type
     */
    private WebDriver createWebDriver(String browser, boolean headless, String gridUrl) {
        if (gridUrl != null && !gridUrl.isEmpty()) {
            return createRemoteWebDriver(browser, headless, gridUrl);
        }

        return switch (browser.toLowerCase()) {
            case "chrome" -> createChromeDriver(headless);
            case "firefox" -> createFirefoxDriver(headless);
            default -> {
                logger.warn("Unknown browser '{}', defaulting to Chrome", browser);
                yield createChromeDriver(headless);
            }
        };
    }

    private WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--proxy-server='direct://'");
        options.addArguments("--proxy-bypass-list=*");
        options.addArguments("--start-maximized");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        return new ChromeDriver(options);
    }

    private WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }

        return new FirefoxDriver(options);
    }

    private WebDriver createRemoteWebDriver(String browser, boolean headless, String gridUrl) {
        try {
            logger.info("Creating Remote WebDriver for browser: {} at {}", browser, gridUrl);

            return switch (browser.toLowerCase()) {
                case "firefox" -> new RemoteWebDriver(
                    URI.create(gridUrl).toURL(),
                    createFirefoxOptions(headless)
                );
                default -> new RemoteWebDriver(
                    URI.create(gridUrl).toURL(),
                    createChromeOptions(headless)
                );
            };
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Selenium Grid URL: " + gridUrl, e);
        }
    }

    private ChromeOptions createChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--proxy-server='direct://'");
        options.addArguments("--proxy-bypass-list=*");
        options.addArguments("--start-maximized");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        return options;
    }

    private FirefoxOptions createFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        return options;
    }

    /**
     * Determine base URL based on scenario tags
     */
    private String determineBaseUrl(Scenario scenario) {
        var tags = scenario.getSourceTagNames();

        if (tags.contains("@Shopping")) {
            return ConfigProperties.getProperty("base.url.shopping");
        } else if (tags.contains("@Amazon")) {
            return ConfigProperties.getProperty("base.url.amazon");
        } else if (tags.contains("@Practice")) {
            return ConfigProperties.getProperty("base.url.practice.page");
        } else if (tags.contains("@GreenKart")) {
            return ConfigProperties.getProperty("base.url.greenkart");
        } else if (tags.contains("@FlightBooking")) {
            return ConfigProperties.getProperty("base.url.flight.booking");
        }

        // Default to shopping if no tag matches
        return ConfigProperties.getProperty("base.url.shopping");
    }
}

