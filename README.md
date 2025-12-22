# Selenium Framework

## Overview
Resilient UI/API automation framework built with Java 25, Maven, and TestNG. It drives Page Object Model coverage for multiple sample applications (GreenKart, Shopping, Amazon, Flight Booking, Practice Page, etc.) while reusing a common `BaseTest`, rich Page Objects, helper utilities (waits, screenshots, API clients, JSON readers), and Allure reporting hooks.

## Tech Stack
- Selenium 4.38.0 (WebDriver, DevTools, waits) orchestrated via WebDriverManager 5.9.2 for automatic binary provisioning.
- TestNG 7.11.0 as the primary runner (suite control, data providers, listeners, parallelism).
- SLF4J logging, Allure TestNG adapter 2.27.0, AShot 1.5.4 for full-page screenshots.
- Jackson 2.18.x, custom `JsonDataHelper`, and REST-assured 5.5.6 for API-driven flows (e.g., shopping authentication token).
- Faker (DataFaker 2.3.1) + Lombok (records, `@SneakyThrows`) for concise, randomized test data factories.
- Maven Surefire 3.2.5 + Allure Maven plugin for CLI/CI integration.

## Project Layout
```
pom.xml
src
├─ main
│  └─ java/com/yehorychev/selenium
│     ├─ config/ConfigProperties.java
│     ├─ helpers/{WaitHelper,ScreenshotHelper,JsonDataHelper,WaitTimeoutException}
│     └─ pages
│        ├─ amazon/
│        ├─ flightbooking/
│        ├─ greenkart/
│        ├─ practice/
│        ├─ shopping/
│        └─ common/{BasePage,components}
└─ test
   ├─ java/com/yehorychev/selenium
   │  ├─ core/BaseTest.java
   │  ├─ listeners/AllureTestListener.java
   │  └─ tests
   │     ├─ amazon/
   │     ├─ flightbooking/
   │     ├─ greenkart/
   │     ├─ practice/
   │     └─ shopping/
   │        ├─ api/ (Rest-Assured clients, DTOs)
   │        ├─ data/ (TestNG data providers, fixtures)
   │        └─ ui/ (TestNG UI test classes)
   └─ resources
      ├─ assets/
      │  ├─ data/*.json
      │  └─ screenshots/
      ├─ config.properties
      └─ testng.xml
```

## Configuration
`src/test/resources/config.properties` centralizes runtime knobs:
- `base.url.*` entries per application (shopping, amazon, practice, etc.).
- Browser profile: `browser.default`, `browser.headless.enabled`, extra args (`browser.headless.args`) consumed when Chrome/Firefox headless is required (CI or Jenkins agents).
- Wait behavior: `wait.default.seconds`, `wait.polling.millis` feed `WaitHelper` defaults (override per action when needed).
- Screenshots: destination folder, failures-only toggle, and AShot scrolling timeout.
- Shopping credentials/card defaults for API authentication or payment flows. Sensitive data can be overridden with JVM flags/environment variables, e.g. `mvn test -Dshopping.password=***`.

Any property is overrideable at runtime: `mvn test -Dbrowser=firefox -Dbase.url.shopping=https://...`.

## Test Data & Fixtures
- **Static fixtures** live under `src/test/resources/assets/data/*.json` (products, passengers, alert texts, etc.) and load through `JsonDataHelper` or dedicated TestNG data providers in `tests/**/data` packages.
- **Dynamic data** (customer profile, cards, emails) comes from `TestDataFactory` (Faker-backed) so tests stay idempotent while covering real-world inputs.
- Shopping API utilities (REST-assured clients) reuse both fixture data and Faker output to bootstrap UI tests with tokens/cookies when necessary.

## Running Tests
Full suite:
```bash
mvn clean test
```
Common overrides:
- Target a specific module: `mvn clean test -Dtest=ShoppingProductsTest` (class) or from IDE.
- Switch browser: `mvn clean test -Dbrowser=firefox` (Chrome, Firefox, Safari supported; Safari guarded by locking due to driver limitations).
- Force headless: `mvn clean test -Dbrowser.headless.enabled=true` (adds `browser.headless.args` to driver options).
- Point to different base site: `mvn clean test -DbaseUrlKey=base.url.practice.page` (read by `BaseTest`).
- Adjust waits/screenshots inline (e.g., `-Dwait.default.seconds=10`).

Parallelism is configured in `testng.xml` (methods-level by default). Increase/decrease `thread-count` depending on infrastructure capacity.

## Screenshots & Diagnostics
- `ScreenshotHelper` (AShot) captures full-page PNGs either for every test or failures only based on configuration; outputs stored under `screenshot.directory` and auto-attached to Allure.
- `WaitHelper` centralizes visibility/clickability waits, page-ready/AJAX checks, scroll + retry actions, and wraps timeout exceptions in `WaitTimeoutException` with descriptive logs.
- Custom logging (SLF4J) surrounds high-level actions, easing triage when combined with Allure attachments and TestNG reports.

## Allure Reporting
Artifacts: `target/allure-results`.
- Start interactive viewer:
```bash
mvn allure:serve
```
- Produce static report (for CI publishing):
```bash
mvn allure:report
```
Attach the generated `target/site/allure-maven-plugin` or raw results to CI artifacts. Steps and assertions leverage Allure steps (via annotations or `Allure.step(...)`) ensuring readable timelines with embedded screenshots/page source when failures occur.

## CI & Headless Guidance
- Jenkins/agents without GUI should use headless mode (`browser.headless.enabled=true`) + GPU disabling flags already present in `config.properties`.
- Browser choice is parameterized; pass `-Dbrowser=<chrome|firefox|safari>` per pipeline/environment.
- Ensure Allure CLI or allure Docker image is available for publishing reports from CI logs.
- Retain `target/screenshots` and `target/allure-results` as build artifacts for debugging when pipelines fail.

## Troubleshooting
- **Amazon storefront** may show location modals; corresponding Page Objects dismiss them, but extra waits might be necessary for geo-specific deployments.
- **Safari parallel issues**: WebDriver limitations prevent concurrent sessions; fallback to sequential or dedicate Safari-only suite.
- **CDP mismatch warnings** appear until Selenium ships matching DevTools version for the installed Chrome build; safe to ignore unless debugging DevTools APIs.
