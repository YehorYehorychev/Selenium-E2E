package com.yehorychev.selenium.pages.practice;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PracticeFooterSection extends BasePage {

    private static final By FOOTER_LINKS = By.cssSelector("li.gf-li a");

    public PracticeFooterSection(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    public List<WebElement> getFooterLinks() {
        return findAllPresent(FOOTER_LINKS);
    }
}
