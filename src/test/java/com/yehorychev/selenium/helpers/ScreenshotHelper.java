package com.yehorychev.selenium.helpers;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotHelper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final Path SCREENSHOT_DIR = Path.of("src", "test", "resources", "assets", "screenshots");

    private ScreenshotHelper() {
        // utility class
    }

    public static void capture(WebDriver driver, String testName) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null when capturing a screenshot");
        }
        if (!(driver instanceof TakesScreenshot)) {
            throw new IllegalStateException("Provided WebDriver does not support screenshots");
        }

        try {
            Files.createDirectories(SCREENSHOT_DIR);
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String safeName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            Path destination = SCREENSHOT_DIR.resolve(timestamp + "-" + safeName + ".png");

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to capture screenshot for test: " + testName, e);
        }
    }
}
