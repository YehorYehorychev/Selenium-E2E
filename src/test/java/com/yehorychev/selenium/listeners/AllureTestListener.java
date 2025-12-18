package com.yehorychev.selenium.listeners;

import io.qameta.allure.Allure;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Lightweight TestNG listener for Allure lifecycle metadata.
 */
public class AllureTestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        Allure.getLifecycle().updateTestCase(testResult ->
                testResult.setName(result.getMethod().getMethodName()));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        attachThrowable(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        attachThrowable(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        onTestFailure(result);
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
    }

    private void attachThrowable(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            Allure.addAttachment("Throwable", throwable.toString());
        }
    }
}
