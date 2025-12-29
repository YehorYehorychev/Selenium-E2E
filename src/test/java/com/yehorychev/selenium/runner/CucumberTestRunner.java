package com.yehorychev.selenium.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG Runner for Cucumber BDD tests.
 * Integrates Cucumber with TestNG and Allure reporting.
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {
        "com.yehorychev.selenium.hooks",
        "com.yehorychev.selenium.steps"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    monochrome = true,
    dryRun = false
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {

    /**
     * Enable parallel execution at scenario level.
     * Thread count controlled by testng.xml or Maven Surefire.
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}

