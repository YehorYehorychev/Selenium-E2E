# Selenium Framework 2026

## Overview
Modular UI/API automation framework built with Java 25, Maven, and TestNG. It exercises Page Object Model, data-driven testing, and hybrid UI/API flows across several sample applications (GreenKart, Shopping, Amazon, Flight Booking, etc.). The stack shares a common `BaseTest`, extensible Page Objects, reusable helpers (waits, screenshots, API), and Allure reporting.

## Key Features
- Full Page Object Model split by business domains/pages.
- Centralized `BaseTest` with resilient `WaitHelper`, WebDriverManager integration, browser selection (Chrome/Firefox/Safari), and thread-safe driver lifecycle management.
- AShot-backed `ScreenshotHelper` capturing full-page PNGs on demand or failures only (configurable).
- REST-assured utilities for Shopping-site authentication plus data-driven records/JSON fixtures.
- Allure wiring with automatic screenshot attachments in `target/allure-results`.
- Faker-powered `TestDataFactory` generating realistic user/payment data per test run while static product/country data lives in JSON fixtures.
- Lombok annotations (`@SneakyThrows`, records) keep helpers concise without sacrificing readability.
- All tests instrumented with Allure steps/severity metadata for CI-friendly reporting.

## Prerequisites
- JDK 25+
- Maven 3.9+
- Local Chrome/Firefox/Safari (WebDriverManager downloads binaries automatically)
- Allure CLI for viewing reports: [installation guide](https://docs.qameta.io/allure/#_installing_a_commandline)

## Project Layout
```
pom.xml
src
├─ main
│  └─ java/com/yehorychev/selenium
│     ├─ config/ConfigProperties.java
│     ├─ helpers/{WaitHelper,ScreenshotHelper,...}
│     └─ pages/{greenKart,shopping,amazon,...}
└─ test
   ├─ java/com/yehorychev/selenium
   │  ├─ core/BaseTest.java
   │  ├─ listeners/AllureTestListener.java
   │  └─ tests/{greenKart,shopping,amazon,...}
   └─ resources
      ├─ config.properties
      └─ testng.xml
```

## Configuration
All runtime settings live in `src/test/resources/config.properties`:
- `base.url.*` – base URLs per site.
- `browser.default`, `wait.*`, `screenshot.*`, shopping credentials.
- Any property can be overridden via JVM arguments (e.g., `-Dbrowser=firefox`).

### Test Data & Data Providers
- Static business data (e.g., shopping product name/price/country) resides in `src/test/resources/assets/data/*.json` and is loaded through TestNG data providers (`ShoppingDataProviders`, etc.).
- Dynamic/sensitive values (names, cards, CVV, expiry) come from `TestDataFactory` (Faker) right inside the tests, so fixtures stay environment-agnostic.
- Additional providers can be added per module under `src/test/java/com/yehorychev/selenium/data` to keep test classes lean.

## Running Tests
Execute the full TestNG suite:
```bash
mvn clean test
```
- Override browser through CLI (`-Dbrowser=safari`) or via `<parameter>` in `testng.xml`.
- To target specific tests: `mvn clean test -Dtest=ShoppingProductsTest` or run from IDE.

### TestNG Parameters
Suite parameters accept `baseUrlKey`/`browser`. To start tests on another site:
```bash
mvn clean test -DbaseUrlKey=base.url.shopping
```

## Allure Reporting
After `mvn test`, artifacts reside in `target/allure-results`.
- Launch local server:
```bash
mvn allure:serve
```
- Generate static report for CI:
```bash
mvn allure:report
```
Publish the folder `target/site/allure-maven-plugin` or archive `target/allure-results` for remote viewers.

### Step Instrumentation
Every test method wraps user actions/assertions in `@Step`-annotated helpers (or `Allure.step` blocks). This guarantees readable timelines in the Allure UI and complements the automatic screenshot/page-source attachments from `BaseTest`.

## Screenshots
- Destination folder: `screenshot.directory`.
- `screenshot.failures.only` and `screenshot.fullpage.*` toggle capture frequency and AShot strategy.
- Each capture is attached to the related Allure test entry automatically.

## Test Data
- Shopping module leverages `ShoppingTestData` records + TestNG data providers.
- JSON fixtures under `src/test/resources` can be expanded for other modules.
- Faker-backed helpers sit in `TestDataFactory`; reuse them whenever random-yet-realistic data is preferable to static fixtures (e.g., registration flows).

## Parallelism & Safari Notes
- Suite runs with `parallel="methods"` and `thread-count="6"`.
- Safari WebDriver is guarded by a `ReentrantLock`, so it runs sequentially (driver limitation). Consider dedicated suites or lower thread count when Safari is required.

## Troubleshooting
- **Amazon tests**: interstitials/geo prompts may appear; `AmazonHomePage` tries to dismiss them, but additional waits or VPN may be needed.
- **Broken links test**: external Udemy link can return 4xx; whitelist or mock if constant failures are undesirable.
- **CDP warning (Chrome 143)**: Selenium 4.38 ships DevTools v142; warning disappears once Selenium adds CDP143 support.
