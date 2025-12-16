package com.yehorychev.selenium.pages.greenkart;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class TopDealsPage extends BasePage {

    private static final By NAME_COLUMN_HEADER = By.xpath("//tr/th[1]");
    private static final By NAME_COLUMN_CELLS = By.xpath("//tr/td[1]");
    private static final By NEXT_BUTTON = By.cssSelector("[aria-label='Next']");

    private final String parentWindowHandle;

    public TopDealsPage(WebDriver driver, WaitHelper waitHelper, String parentWindowHandle) {
        super(driver, waitHelper);
        this.parentWindowHandle = parentWindowHandle;
    }

    public void sortNameColumn() {
        click(NAME_COLUMN_HEADER);
    }

    public List<String> getDisplayedNames() {
        return findAll(NAME_COLUMN_CELLS)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public String getPriceFor(String veggieName) {
        List<String> prices;
        do {
            List<WebElement> rows = findAll(NAME_COLUMN_CELLS);
            prices = rows.stream()
                    .filter(row -> row.getText().contains(veggieName))
                    .map(TopDealsPage::extractPrice)
                    .collect(Collectors.toList());

            if (prices.isEmpty()) {
                safeClick(NEXT_BUTTON);
            }
        } while (prices.isEmpty());

        return prices.get(0);
    }

    private static String extractPrice(WebElement element) {
        return element.findElement(By.xpath("following-sibling::td[1]")).getText();
    }

    public void closeAndReturn() {
        driver.close();
        switchToParentWindow(parentWindowHandle);
    }
}
