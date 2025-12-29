package com.yehorychev.selenium.steps.practice;

import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.pages.practice.PracticeAlertsPage;
import com.yehorychev.selenium.pages.practice.PracticeFooterSection;
import com.yehorychev.selenium.pages.practice.PracticeTableSection;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PracticeSteps {

    private final ScenarioContext context;

    public PracticeSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the practice page is opened")
    public void thePracticePageIsOpened() {
        PracticeAlertsPage alertsPage = new PracticeAlertsPage(context.getDriver(), context.getWaitHelper());
        context.set("alertsPage", alertsPage);
    }

    @When("I enter name {string}")
    public void iEnterName(String name) {
        PracticeAlertsPage alertsPage = context.get("alertsPage");
        alertsPage.enterName(name);
    }

    @When("I trigger the confirm alert")
    public void iTriggerTheConfirmAlert() {
        PracticeAlertsPage alertsPage = context.get("alertsPage");
        alertsPage.triggerConfirmAlert();
    }

    @Then("the alert text should be {string}")
    public void theAlertTextShouldBe(String expectedText) {
        PracticeAlertsPage alertsPage = context.get("alertsPage");
        Assert.assertEquals(alertsPage.readAlertText(), expectedText);
    }

    @When("I dismiss the alert")
    public void iDismissTheAlert() {
        PracticeAlertsPage alertsPage = context.get("alertsPage");
        alertsPage.dismissAlert();
    }

    @Then("the alert should be closed")
    public void theAlertShouldBeClosed() {
        PracticeAlertsPage alertsPage = context.get("alertsPage");
        alertsPage.waitForAlertDismissed();
    }

    @When("I scroll the window down by {int} pixels")
    public void iScrollTheWindowDownByPixels(int pixels) {
        PracticeAlertsPage alertsPage = context.get("alertsPage");
        alertsPage.scrollWindowBy(0, pixels);
    }

    @Then("the web table section should be visible")
    public void theWebTableSectionShouldBeVisible() {
        PracticeAlertsPage alertsPage = context.get("alertsPage");
        Assert.assertTrue(alertsPage.getWebTableSection().isDisplayed());
    }

    @When("I scroll the fixed table to bottom")
    public void iScrollTheFixedTableToBottom() {
        PracticeAlertsPage alertsPage = context.get("alertsPage");
        alertsPage.scrollFixedTableToBottom();
    }

    @Then("I should see table values")
    public void iShouldSeeTableValues() {
        PracticeTableSection tableSection = new PracticeTableSection(context.getDriver(), context.getWaitHelper());
        List<String> rowValues = tableSection.readFourthRowValues();
        if (rowValues.size() != 9) {
            rowValues = tableSection.readFourthColumnValues();
        }
        Assert.assertFalse(rowValues.isEmpty(), "Expected values for table row/column, but none were found");
    }

    @When("I check all footer links")
    public void iCheckAllFooterLinks() {
        PracticeFooterSection footerSection = new PracticeFooterSection(context.getDriver(), context.getWaitHelper());
        context.set("footerSection", footerSection);
    }

    @Then("all links should return valid status codes")
    @SneakyThrows
    public void allLinksShouldReturnValidStatusCodes() {
        PracticeFooterSection footerSection = context.get("footerSection");
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

