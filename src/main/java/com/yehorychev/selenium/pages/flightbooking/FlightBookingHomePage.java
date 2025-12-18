package com.yehorychev.selenium.pages.flightbooking;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlightBookingHomePage extends BasePage {

    private static final By CURRENCY_DROPDOWN = By.id("ctl00_mainContent_DropDownListCurrency");
    private static final By PASSENGER_INFO = By.id("divpaxinfo");
    private static final By ADULT_PLUS_BTN = By.id("hrefIncAdt");
    private static final By CHILD_PLUS_BTN = By.id("hrefIncChd");
    private static final By PASSENGER_DONE_BTN = By.id("btnclosepaxoption");
    private static final By ORIGIN_INPUT = By.id("ctl00_mainContent_ddl_originStation1_CTXT");
    private static final By DESTINATION_INPUT = By.id("ctl00_mainContent_ddl_destinationStation1_CTXT");
    private static final By COUNTRY_AUTOSUGGEST = By.id("autosuggest");
    private static final By COUNTRY_SUGGESTIONS = By.xpath("//li[@class='ui-menu-item']/a");
    private static final Pattern ADULT_PATTERN = Pattern.compile("(\\d+)\\s+Adult");
    private static final Pattern CHILD_PATTERN = Pattern.compile("(\\d+)\\s+Child");

    public FlightBookingHomePage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public void setCurrency(String currencyCode) {
        Select dropdown = new Select(find(CURRENCY_DROPDOWN));
        dropdown.selectByVisibleText(currencyCode);
        waitUntil(d -> currencyCode.equalsIgnoreCase(dropdown.getFirstSelectedOption().getText().trim()));
    }

    public String getSelectedCurrency() {
        return new Select(find(CURRENCY_DROPDOWN)).getFirstSelectedOption().getText();
    }

    public void openPassengerSelector() {
        click(PASSENGER_INFO);
    }

    public void addAdults(int desiredCount) {
        if (desiredCount < 1) {
            throw new IllegalArgumentException("At least one adult is required");
        }
        adjustPassengerCount(desiredCount, getCurrentAdults(), ADULT_PLUS_BTN);
    }

    public void addChildren(int desiredCount) {
        if (desiredCount < 0) {
            throw new IllegalArgumentException("Children count cannot be negative");
        }
        adjustPassengerCount(desiredCount, getCurrentChildren(), CHILD_PLUS_BTN);
    }

    public void confirmPassengers() {
        click(PASSENGER_DONE_BTN);
    }

    public String getPassengerInfo() {
        return getText(PASSENGER_INFO);
    }

    public void selectOrigin(String cityCode) {
        click(ORIGIN_INPUT);
        safeClick(By.xpath("//a[@value='" + cityCode + "']"));
    }

    public void selectDestination(String cityCode) {
        click(DESTINATION_INPUT);
        safeClick(By.xpath("//a[@value='" + cityCode + "']"));
    }

    public String getSelectedOrigin() {
        return getAttribute(ORIGIN_INPUT, "value");
    }

    public String getSelectedDestination() {
        return getAttribute(DESTINATION_INPUT, "value");
    }

    public void searchCountry(String partialName) {
        type(COUNTRY_AUTOSUGGEST, partialName);
        findAll(COUNTRY_SUGGESTIONS);
    }

    public void chooseCountrySuggestion(String countryName) {
        findAll(COUNTRY_SUGGESTIONS).stream()
                .filter(option -> option.getText().equalsIgnoreCase(countryName))
                .findFirst()
                .ifPresent(WebElement::click);
    }

    public String getSelectedCountry() {
        return getAttribute(COUNTRY_AUTOSUGGEST, "value");
    }

    private void adjustPassengerCount(int desired, int current, By incrementLocator) {
        for (int i = current; i < desired; i++) {
            click(incrementLocator);
        }
    }

    private int getCurrentAdults() {
        return extractCount(ADULT_PATTERN, getPassengerInfo(), 1);
    }

    private int getCurrentChildren() {
        return extractCount(CHILD_PATTERN, getPassengerInfo(), 0);
    }

    private int extractCount(Pattern pattern, String text, int defaultValue) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return defaultValue;
    }
}
