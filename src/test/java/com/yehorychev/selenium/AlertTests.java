package com.yehorychev.selenium;

import com.yehorychev.selenium.pages.practice.PracticeAlertsPage;
import com.yehorychev.selenium.pages.practice.PracticeFooterSection;
import com.yehorychev.selenium.pages.practice.PracticeTableSection;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AlertTests extends BaseTest {

    @Override
    protected String getDefaultBaseUrlKey() {
        return "base.url.practice.page";
    }

    @Test
    void dismissAlertTest() {
        PracticeAlertsPage alertsPage = new PracticeAlertsPage(driver, waitHelper);
        alertsPage.enterName("Yehor");
        alertsPage.triggerConfirmAlert();

        String alertText = alertsPage.readAlertText();
        Assert.assertEquals(alertText, "Hello Yehor, Are you sure you want to confirm?");

        alertsPage.dismissAlert();
        alertsPage.waitForAlertDismissed();
    }

    @Test
    void scrollPageTest() {
        PracticeAlertsPage alertsPage = new PracticeAlertsPage(driver, waitHelper);
        PracticeTableSection tableSection = new PracticeTableSection(driver, waitHelper);

        alertsPage.scrollWindowBy(0, 500);
        Assert.assertTrue(alertsPage.getWebTableSection().isDisplayed());

        alertsPage.scrollFixedTableToBottom();
        List<String> rowValues = tableSection.readFourthRowValues();

        if (rowValues.size() != 9) {
            rowValues = tableSection.readFourthColumnValues();
        }

        Assert.assertFalse(rowValues.isEmpty(), "Expected values for table row/column, but none were found");
    }

    @Test
    void brokenLinksTest() throws IOException {
        PracticeFooterSection footerSection = new PracticeFooterSection(driver, waitHelper);
        SoftAssert softAssert = new SoftAssert();

        List<WebElement> links = footerSection.getFooterLinks();
        for (WebElement link : links) {
            String href = link.getAttribute("href");
            if (href == null || href.isBlank()) {
                continue;
            }

            HttpURLConnection connection = (HttpURLConnection) new URL(href).openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode >= 400) {
                softAssert.fail(String.format("Broken link: %s (Response code: %d)", href, responseCode));
            }
        }

        softAssert.assertAll();
    }
}
