package com.yehorychev.selenium.tests.practice;

import com.yehorychev.selenium.core.BaseTest;
import com.yehorychev.selenium.data.PracticeDataProviders;
import com.yehorychev.selenium.data.PracticeDataProviders.PracticeAlertData;
import com.yehorychev.selenium.pages.practice.PracticeAlertsPage;
import com.yehorychev.selenium.pages.practice.PracticeFooterSection;
import com.yehorychev.selenium.pages.practice.PracticeTableSection;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
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

    @Test(dataProvider = "practiceAlerts", dataProviderClass = PracticeDataProviders.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validate confirm alert text and ability to dismiss it")
    void dismissAlertTest(PracticeAlertData data) {
        PracticeAlertsPage alertsPage = openAlertsPage();
        enterName(alertsPage, data.name());
        triggerConfirmAlert(alertsPage);
        verifyAlertText(alertsPage, data.expectedAlert());
        dismissAlert(alertsPage);
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    void scrollPageTest() {
        PracticeAlertsPage alertsPage = openAlertsPage();
        PracticeTableSection tableSection = openTableSection();

        scrollWindow(alertsPage);
        verifyWebTableVisible(alertsPage);
        scrollFixedTable(alertsPage);
        verifyTableValues(tableSection);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Check footer links for broken responses")
    void brokenLinksTest() throws IOException {
        PracticeFooterSection footerSection = openFooterSection();
        validateLinks(footerSection);
    }

    @Step("Open alerts page")
    private PracticeAlertsPage openAlertsPage() {
        return Allure.step("Instantiate PracticeAlertsPage", () -> new PracticeAlertsPage(driver(), waitHelper()));
    }

    @Step("Open table section")
    private PracticeTableSection openTableSection() {
        return Allure.step("Instantiate PracticeTableSection", () -> new PracticeTableSection(driver(), waitHelper()));
    }

    @Step("Open footer section")
    private PracticeFooterSection openFooterSection() {
        return Allure.step("Instantiate PracticeFooterSection", () -> new PracticeFooterSection(driver(), waitHelper()));
    }

    @Step("Enter name: {name}")
    private void enterName(PracticeAlertsPage alertsPage, String name) {
        Allure.step("Enter name into alert field", () -> alertsPage.enterName(name));
    }

    @Step("Trigger confirm alert")
    private void triggerConfirmAlert(PracticeAlertsPage alertsPage) {
        Allure.step("Trigger confirm alert", alertsPage::triggerConfirmAlert);
    }

    @Step("Verify alert text")
    private void verifyAlertText(PracticeAlertsPage alertsPage, String expected) {
        Allure.step("Verify alert text", () -> Assert.assertEquals(alertsPage.readAlertText(), expected));
    }

    @Step("Dismiss alert and wait until hidden")
    private void dismissAlert(PracticeAlertsPage alertsPage) {
        Allure.step("Dismiss alert", () -> {
            alertsPage.dismissAlert();
            alertsPage.waitForAlertDismissed();
        });
    }

    @Step("Scroll window down")
    private void scrollWindow(PracticeAlertsPage alertsPage) {
        Allure.step("Scroll window down", () -> alertsPage.scrollWindowBy(0, 500));
    }

    @Step("Verify web table section visible")
    private void verifyWebTableVisible(PracticeAlertsPage alertsPage) {
        Allure.step("Verify table visibility", () -> Assert.assertTrue(alertsPage.getWebTableSection().isDisplayed()));
    }

    @Step("Scroll fixed table to bottom")
    private void scrollFixedTable(PracticeAlertsPage alertsPage) {
        Allure.step("Scroll fixed table to bottom", alertsPage::scrollFixedTableToBottom);
    }

    @Step("Verify table values exist")
    private void verifyTableValues(PracticeTableSection tableSection) {
        Allure.step("Verify fixed table values", () -> {
            List<String> rowValues = tableSection.readFourthRowValues();
            if (rowValues.size() != 9) {
                rowValues = tableSection.readFourthColumnValues();
            }
            Assert.assertFalse(rowValues.isEmpty(), "Expected values for table row/column, but none were found");
        });
    }

    @Step("Validate footer links")
    private void validateLinks(PracticeFooterSection footerSection) throws IOException {
        Allure.step("Validate footer links", () -> {
            SoftAssert softAssert = new SoftAssert();
            List<WebElement> links = footerSection.getFooterLinks();
            for (WebElement link : links) {
                String href = link.getAttribute("href");
                if (href == null || href.isBlank()) {
                    continue;
                }

                int responseCode = fetchStatusCode(href);
                if (responseCode >= 400) {
                    softAssert.fail(String.format("Broken link: %s (Response code: %d)", href, responseCode));
                }
            }
            softAssert.assertAll();
        });
    }

    @Step("Fetch status code for URL")
    private int fetchStatusCode(String href) {
        return Allure.step("Fetch status code", () -> {
            HttpURLConnection connection = (HttpURLConnection) new URL(href).openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            return connection.getResponseCode();
        });
    }
}
