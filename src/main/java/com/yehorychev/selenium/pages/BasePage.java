package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.helpers.WaitHelper;
import org.openqa.selenium.WebDriver;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitHelper waitHelper;

    protected BasePage(WebDriver driver, WaitHelper waitHelper) {
        this.driver = driver;
        this.waitHelper = waitHelper;
    }

    public String getTitle() {
        return driver.getTitle();
    }
}

