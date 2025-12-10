package com.yehorychev.selenium.pages.practice;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PracticeTableSection extends BasePage {

    private static final By FIXED_HEADER_TABLE = By.cssSelector(".tableFixHead");
    private static final By FIXED_HEADER_ROWS = By.cssSelector(".tableFixHead tr");

    public PracticeTableSection(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public void scrollTableToBottom() {
        jsExecutor().executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", waitHelper.visibilityOf(FIXED_HEADER_TABLE));
    }

    public List<String> readFourthRowValues() {
        List<WebElement> rows = waitHelper.presenceOfAllElements(FIXED_HEADER_ROWS);
        if (rows.size() < 4) {
            return new ArrayList<>();
        }
        List<WebElement> cells = rows.get(3).findElements(By.tagName("td"));
        return cells.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<String> readFourthColumnValues() {
        return waitHelper.presenceOfAllElements(By.cssSelector(".tableFixHead td:nth-child(4)")).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}

