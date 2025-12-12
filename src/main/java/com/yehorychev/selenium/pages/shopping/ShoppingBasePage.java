package com.yehorychev.selenium.pages.shopping;

import com.yehorychev.selenium.helpers.WaitHelper;
import com.yehorychev.selenium.pages.common.BasePage;
import org.openqa.selenium.WebDriver;

public abstract class ShoppingBasePage extends BasePage {

    protected ShoppingBasePage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }
}

