package com.yehorychev.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class InitTest {
    public static void main(String[] args) throws InterruptedException {

        WebDriver driver = new ChromeDriver();
        driver.get("https://google.com");
        Thread.sleep(2000);

        System.out.println(driver.getTitle());

        driver.quit();
    }
}