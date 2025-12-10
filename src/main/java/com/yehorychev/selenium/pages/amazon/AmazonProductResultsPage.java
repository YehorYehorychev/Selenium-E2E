package com.yehorychev.selenium.pages.amazon;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class AmazonProductResultsPage extends BasePage {

    private static final By RESULTS_CONTAINER = By.cssSelector("div.s-main-slot.s-result-list");
    private static final By RESULT_ITEMS = By.cssSelector("div.s-main-slot .s-result-item");
    private static final By SEARCH_BAR = By.id("twotabsearchtextbox");

    public AmazonProductResultsPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
        waitHelper.visibilityOf(RESULTS_CONTAINER);
    }

    public List<String> getSearchResults() {
        List<WebElement> items = driver.findElements(RESULT_ITEMS);
        return items.stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    public String getSearchBoxValue() {
        WebElement searchBar = waitHelper.visibilityOf(SEARCH_BAR);
        return searchBar.getAttribute("value");
    }
}

