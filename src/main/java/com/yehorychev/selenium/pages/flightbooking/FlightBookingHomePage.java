package com.yehorychev.selenium.pages.flightbooking;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

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

    public FlightBookingHomePage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public void setCurrency(String currencyCode) {
        WebElement dropdownElement = find(CURRENCY_DROPDOWN);
        Select dropdown = new Select(dropdownElement);
        dropdown.selectByVisibleText(currencyCode);
        waitHelper.until(d -> currencyCode.equalsIgnoreCase(dropdown.getFirstSelectedOption().getText().trim()));
    }

    public String getSelectedCurrency() {
        Select dropdown = new Select(find(CURRENCY_DROPDOWN));
        return dropdown.getFirstSelectedOption().getText();
    }

    public void openPassengerSelector() {
        click(PASSENGER_INFO);
    }

    public void addAdults(int count) {
        WebElement adultPlus = waitHelper.visibilityOf(ADULT_PLUS_BTN);
        for (int i = 0; i < count; i++) {
            adultPlus.click();
        }
    }

    public void addChildren(int count) {
        WebElement childPlus = waitHelper.visibilityOf(CHILD_PLUS_BTN);
        for (int i = 0; i < count; i++) {
            childPlus.click();
        }
    }

    public void confirmPassengers() {
        click(PASSENGER_DONE_BTN);
    }

    public String getPassengerInfo() {
        return getText(PASSENGER_INFO);
    }

    public void selectOrigin(String cityCode) {
        click(ORIGIN_INPUT);
        click(By.xpath("//a[@value='" + cityCode + "']"));
    }

    public void selectDestination(String cityCode) {
        click(DESTINATION_INPUT);
        click(By.xpath("//a[@value='" + cityCode + "']"));
    }

    public String getSelectedOrigin() {
        return find(ORIGIN_INPUT).getAttribute("value");
    }

    public String getSelectedDestination() {
        return find(DESTINATION_INPUT).getAttribute("value");
    }

    public void searchCountry(String partialName) {
        type(COUNTRY_AUTOSUGGEST, partialName);
        waitHelper.visibilityOfAllElements(COUNTRY_SUGGESTIONS);
    }

    public void chooseCountrySuggestion(String countryName) {
        List<WebElement> suggestions = findAll(COUNTRY_SUGGESTIONS);
        suggestions.stream()
                .filter(option -> option.getText().equalsIgnoreCase(countryName))
                .findFirst()
                .ifPresent(WebElement::click);
    }

    public String getSelectedCountry() {
        return find(COUNTRY_AUTOSUGGEST).getAttribute("value");
    }
}
