package com.yehorychev.selenium.helpers;

import com.yehorychev.selenium.config.ConfigProperties;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.shooting.cutter.FixedCutStrategy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotHelper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotHelper() {
        // utility class
    }

    public static void capture(WebDriver driver, String testName) {
        capture(driver, testName, ConfigProperties.getScreenshotDirectory());
    }

    public static void capture(WebDriver driver, String testName, Path directory) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null when capturing a screenshot");
        }

        try {
            Files.createDirectories(directory);
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String safeName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            Path destination = directory.resolve(timestamp + "-" + safeName + ".png");

            BufferedImage image = ConfigProperties.isFullPageScreenshotsEnabled()
                    ? takeFullPageScreenshot(driver)
                    : takeViewportScreenshot(driver);

            ImageIO.write(image, "png", destination.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to capture screenshot for test: " + testName, e);
        }
    }

    private static BufferedImage takeFullPageScreenshot(WebDriver driver) {
        try {
            return new AShot()
                    .coordsProvider(new WebDriverCoordsProvider())
                    .shootingStrategy(ShootingStrategies.viewportPasting((int) ConfigProperties.getFullPageScrollTimeoutMillis()))
                    .takeScreenshot(driver)
                    .getImage();
        } catch (RuntimeException e) {
            // fallback to regular screenshot if AShot fails
            return takeViewportScreenshot(driver);
        }
    }

    private static BufferedImage takeViewportScreenshot(WebDriver driver) {
        if (!(driver instanceof TakesScreenshot takesScreenshot)) {
            throw new IllegalStateException("Provided WebDriver does not support screenshots");
        }

        try {
            byte[] pngBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            return ImageIO.read(new ByteArrayInputStream(pngBytes));
        } catch (WebDriverException | IOException e) {
            throw new RuntimeException("Failed to capture viewport screenshot", e);
        }
    }
}
