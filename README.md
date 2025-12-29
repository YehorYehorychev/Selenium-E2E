# Selenium Framework 2026

## Repository Branches

This repository maintains two parallel implementations:

### 🌿 **`main` branch** - TestNG Framework
Traditional TestNG-based automation framework with:
- Direct TestNG test classes with `@Test` annotations
- TestNG data providers for parameterization
- TestNG suite XML files for execution control
- Page Object Model with direct method calls from tests

### 🥒 **`cucumber` branch** - Cucumber BDD Framework
Behavior-Driven Development implementation with:
- **Gherkin feature files** (`.feature`) with Given-When-Then scenarios
- **Step definitions** mapping Gherkin steps to Java code
- **Scenario context** for sharing state between steps
- **Examples tables** for data-driven scenarios
- **Cucumber TestNG runner** for execution
- Same Page Objects, helpers, and infrastructure as `main`

**Choose the branch based on your preference:**
- Use **`main`** for traditional TestNG approach with programmatic test definitions
- Use **`cucumber`** for BDD style with business-readable Gherkin scenarios

Both branches share:
- ✅ Same tech stack (Selenium, Allure, Docker, Jenkins)
- ✅ Same Page Object Model
- ✅ Same helper utilities (WaitHelper, ScreenshotHelper, etc.)
- ✅ Same CI/CD setup (Jenkinsfile, docker-compose.yml)
- ✅ Same configuration management (config.properties)

---

## Overview
Resilient UI/API automation framework built with Java 21+, Maven, and TestNG/Cucumber. It drives Page Object Model coverage for multiple sample applications (GreenKart, Shopping, Amazon, Flight Booking, Practice Page, etc.) while reusing a common `BaseTest`, rich Page Objects, helper utilities (waits, screenshots, API clients, JSON readers), and Allure reporting hooks.

## Tech Stack
- Selenium 4.38.0 (WebDriver, RemoteWebDriver, DevTools, waits) orchestrated via WebDriverManager 5.9.2 for automatic binary provisioning.
- **TestNG 7.11.0** as the primary runner (suite control, data providers, listeners, parallelism).
- **Cucumber 7.33.0** (on `cucumber` branch) - BDD framework with Gherkin syntax for business-readable test scenarios.
- SLF4J logging, Allure TestNG adapter 2.27.0, AShot 1.5.4 for full-page screenshots.
- Jackson 2.18.x, custom `JsonDataHelper`, and REST-assured 5.5.6 for API-driven flows (e.g., shopping authentication token).
- Faker (DataFaker 2.3.1) + Lombok (records, `@SneakyThrows`) for concise, randomized test data factories.
- Maven Surefire 3.2.5 + Allure Maven plugin for CLI/CI integration.
- Docker Compose for Jenkins + Selenium Grid CI environment.

## Project Layout

### Common Structure (both branches)
```
pom.xml
Jenkinsfile
docker-compose.yml          (Jenkins + Selenium Grid standalone containers)
Dockerfile.jenkins          (Custom Jenkins image with Java 21, Maven, Allure)
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
```

### TestNG Branch (`main`)
```
└─ test
   ├─ java/com/yehorychev/selenium
   │  ├─ core/BaseTest.java
   │  ├─ listeners/AllureTestListener.java
   │  └─ tests
   │     ├─ amazon/ui/
   │     ├─ flightbooking/ui/
   │     ├─ greenkart/ui/
   │     ├─ practice/ui/
   │     └─ shopping/
   │        ├─ api/ (Rest-Assured clients, DTOs)
   │        ├─ data/ (TestNG data providers, fixtures)
   │        └─ ui/ (TestNG UI test classes)
   └─ resources
      ├─ assets/
      │  ├─ data/*.json
      │  └─ screenshots/
      ├─ config.properties
      └─ testng*.xml          (Multiple suite files per module)
```

### Cucumber Branch (`cucumber`)
```
└─ test
   ├─ java/com/yehorychev/selenium
   │  ├─ core/BaseTest.java
   │  ├─ runner/CucumberTestRunner.java
   │  ├─ context/ScenarioContext.java
   │  ├─ hooks/CucumberHooks.java
   │  ├─ listeners/AllureTestListener.java
   │  ├─ steps/                (Step definitions)
   │  │  ├─ amazon/
   │  │  ├─ flightbooking/
   │  │  ├─ greenkart/
   │  │  ├─ practice/
   │  │  └─ shopping/
   │  └─ tests/shopping/api/    (Rest-Assured clients, DTOs)
   └─ resources
      ├─ assets/data/*.json
      ├─ features/              (Gherkin scenarios)
      │  ├─ amazon.feature
      │  ├─ flightbooking.feature
      │  ├─ greenkart.feature
      │  ├─ practice.feature
      │  └─ shopping.feature
      ├─ config.properties
      └─ testng-cucumber.xml
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

### TestNG Branch (`main`)
Full suite:
```bash
mvn clean test
```

Run specific suite:
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-shopping.xml
```

Common overrides:
- Target a specific module: `mvn clean test -Dtest=ShoppingProductsTest` (class) or from IDE.
- Switch browser: `mvn clean test -Dbrowser=firefox` (Chrome, Firefox, Safari supported; Safari guarded by locking due to driver limitations).
- Force headless: `mvn clean test -Dbrowser.headless.enabled=true` (adds `browser.headless.args` to driver options).
- Point to different base site: `mvn clean test -DbaseUrlKey=base.url.practice.page` (read by `BaseTest`).
- Adjust waits/screenshots inline (e.g., `-Dwait.default.seconds=10`).

Parallelism is configured in `testng.xml` (methods-level by default). Increase/decrease `thread-count` depending on infrastructure capacity.

### Cucumber Branch (`cucumber`)
Run all Cucumber scenarios:
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-cucumber.xml
```

Run with Cucumber tags:
```bash
mvn clean test -Dcucumber.filter.tags="@Shopping"
mvn clean test -Dcucumber.filter.tags="@Critical and not @Broken"
```

Run specific feature file:
```bash
mvn clean test -Dcucumber.features=src/test/resources/features/shopping.feature
```

**Cucumber-specific features:**
- **Gherkin scenarios** in `.feature` files describe test cases in plain English
- **Examples tables** in Scenario Outlines provide data-driven testing
- **Step definitions** map Gherkin steps to Java code
- **ScenarioContext** shares state between steps within a scenario
- **Hooks** (`@Before`, `@After`) handle WebDriver lifecycle per scenario

Same runtime overrides apply:
- Browser selection: `-Dbrowser=chrome`
- Headless mode: `-Dbrowser.headless.enabled=true`
- Configuration properties: `-Dbase.url.shopping=https://...`

## Screenshots & Diagnostics
- `ScreenshotHelper` (AShot) captures full-page PNGs either for every test or failures only based on configuration; outputs stored under `screenshot.directory` and auto-attached to Allure.
- `WaitHelper` centralizes visibility/clickability waits, page-ready/AJAX checks, scroll + retry actions, and wraps timeout exceptions in `WaitTimeoutException` with descriptive logs.
- Custom logging (SLF4J) surrounds high-level actions, easing triage when combined with Allure attachments and TestNG reports.

## Allure Reporting

### Understanding Allure Reports
Allure consists of two components:
1. **Raw data** (`target/allure-results/`) - JSON files with test execution data, screenshots, logs
2. **HTML report** - Interactive web dashboard generated from the raw data

### Local Report Generation
**Option 1: Interactive viewer** (auto-refreshes browser):
```bash
mvn allure:serve
```

**Option 2: Static HTML report** (for sharing):
```bash
mvn allure:report
# Report available at: target/site/allure-maven-plugin/index.html
```

You can also use Allure CLI directly:
```bash
# Install Allure CLI (one-time setup)
brew install allure  # macOS
# or download from https://github.com/allure-framework/allure2/releases

# Generate and open report
allure serve target/allure-results
```

### Jenkins Integration
When tests run in Jenkins:
1. Tests execute and populate `target/allure-results/` with JSON artifacts
2. Jenkins Allure plugin automatically:
   - Generates HTML report from the raw data
   - Publishes it to Jenkins workspace
   - Adds **"Allure Report"** button on build page sidebar
3. Click the button to view interactive dashboard with:
   - Test execution trends (passed/failed/broken over time)
   - Test suites and categories
   - Full-page screenshots attached to failed tests
   - Step-by-step execution timeline
   - Browser logs and page source
   - Severity and feature tags

**Important:** The "Allure Report" button appears in the left sidebar of the build page, NOT in the main artifacts section. If you don't see it:

1. **Ensure Allure Jenkins plugin is installed:**
   - Navigate to: Manage Jenkins → Plugins → Available plugins
   - Search for "Allure Jenkins Plugin"
   - Install and restart Jenkins

2. **Configure Allure commandline tool:**
   
   **Option A: Use Pre-installed Allure (Recommended)**
   - The custom `Dockerfile.jenkins` includes Allure 2.27.0 pre-installed
   - Navigate to: Manage Jenkins → Tools
   - Scroll to "Allure Commandline installations"
   - Click "Add Allure Commandline"
   - Name: `Allure` (must match exactly - used in Jenkinsfile)
   - Installation directory: `/opt/allure-2.27.0`
   - **Uncheck** "Install automatically" (already in Docker image)
   - Save configuration

   **Option B: Let Jenkins Auto-Install**
   - Navigate to: Manage Jenkins → Tools
   - Scroll to "Allure Commandline installations"
   - Click "Add Allure Commandline"
   - Name: `Allure` (must match exactly)
   - **Check** "Install automatically"
   - Choose version: `2.27.0` or latest
   - Save configuration

3. **Verify report generation:**
   - Rebuild the Docker image: `docker compose build jenkins`
   - Restart services: `docker compose up -d`
   - Check build logs for "Generating Allure report..." message
   - Verify `target/allure-results/` contains `.json` files (look in archived artifacts)
   - The button should appear after the first successful report generation

4. **Common issues:**
   - **"ERROR: Allure commandline 'Allure' doesn't exist"**: 
     - The tool name in Jenkins Tools must match exactly `Allure`
     - Or rebuild Docker image with: `docker compose build --no-cache jenkins`
   - **Button still missing**: Try running the job twice - sometimes the button appears after the second build
   - **Empty report**: Check that tests actually ran and produced allure-results (look for JSON files in artifacts)

### What Gets Attached to Reports
Steps and assertions leverage Allure annotations (`@Step`, `@Severity`, `@Description`) and `Allure.step(...)` blocks ensuring readable timelines with:
- Full-page screenshots (via AShot) on failures
- Page source HTML for DOM inspection
- Browser console logs
- API request/response payloads
- Custom attachments (JSON data, test parameters)

## CI & Headless Guidance

### Local Execution
Tests automatically use WebDriverManager to download and configure browser drivers. Headless mode can be enabled in `config.properties`:
```properties
browser.headless.enabled=true
```

### Jenkins/Docker Execution
The framework auto-detects CI environments (`JENKINS_HOME`, `CI` env vars) and automatically:
- Forces headless mode for stability
- Applies Docker-compatible Chrome/Firefox flags (`--no-sandbox`, `--disable-dev-shm-usage`)
- Switches to RemoteWebDriver when `selenium.grid.url` system property or `SELENIUM_GRID_URL` env var is set

#### Custom Jenkins Image
The `Dockerfile.jenkins` builds a customized Jenkins container with:
- **Java 21 (OpenJDK)** - Matches framework requirements
- **Maven 3.9.9** - Pre-installed for faster builds
- **Allure 2.27.0** - Pre-configured at `/opt/allure-2.27.0` for HTML report generation
- **Docker CLI** - Enables Docker-in-Docker scenarios if needed
- **CSP Policy** - Custom Content Security Policy allowing Allure report JavaScript/CSS to load properly

The CSP configuration in `docker-compose.yml` (`JAVA_OPTS=-Dhudson.model.DirectoryBrowserSupport.CSP=...`) is required for Allure HTML reports to render correctly in Jenkins UI. Without it, reports show infinite loading due to blocked inline scripts.

#### Architecture
Docker Compose orchestrates three services:
1. **Jenkins** - CI/CD server with Java 21, Maven, and Allure pre-installed
2. **Chrome Standalone Grid** - Selenium Grid with Chrome browser (ARM64-compatible via seleniarm)
3. **Firefox Standalone Grid** - Selenium Grid with Firefox browser (ARM64-compatible via seleniarm)

Each browser runs in its own isolated container with built-in Grid hub. This provides:
- ✅ Simple setup and configuration (no separate hub required)
- ✅ Independent browser isolation
- ✅ Easy scaling (add container = add browser capacity)
- ✅ Separate Grid UI per browser for better debugging
- ✅ Built-in VNC server for real-time test watching
- ✅ Apple Silicon (M1/M2/M3) native support via seleniarm images

#### Quick Start with Docker Compose

```bash
# Start all services (Jenkins + Chrome Grid + Firefox Grid)
docker compose up -d

# Check status
docker ps

# Access services
# Jenkins: http://localhost:8080
# Chrome Grid: http://localhost:4444/ui
# Firefox Grid: http://localhost:4445/ui
# Chrome VNC: http://localhost:7900 (password: secret)
# Firefox VNC: http://localhost:7901 (password: secret)
```

**Notes:** 
- Use `docker compose` (without hyphen) for Docker Compose v2
- On Apple Silicon (M1/M2/M3), uses `seleniarm/standalone-chromium` and `seleniarm/standalone-firefox` optimized for ARM64 architecture
- On Intel/AMD64, Docker will automatically use AMD64 layers from the same images

#### Accessing Services

| Service | URL | Description |
|---------|-----|-------------|
| Jenkins UI | http://localhost:8080 | CI/CD dashboard and job management |
| Chrome Grid UI | http://localhost:4444/ui | Grid status and session management |
| Firefox Grid UI | http://localhost:4445/ui | Firefox Grid status (if running) |
| Chrome VNC | http://localhost:7900 | Watch Chrome tests live (password: `secret`) |
| Firefox VNC | http://localhost:7901 | Watch Firefox tests live (password: `secret`) |

**VNC Viewing:** Connect to VNC ports to watch browser automation in real-time - invaluable for debugging flaky tests or understanding complex interactions.

#### Running Tests via Selenium Grid
```bash
# Local machine pointing to Chrome Grid
mvn test -Dselenium.grid.url=http://localhost:4444/wd/hub -Dbrowser=chrome

# Firefox Grid
mvn test -Dselenium.grid.url=http://localhost:4445/wd/hub -Dbrowser=firefox

# Inside Jenkins pipeline (automatic via Jenkinsfile)
mvn test -Dbrowser=chrome -DsuiteXmlFile=testng-shopping.xml
```

The `Jenkinsfile` automatically selects the correct Grid URL based on the `BROWSER` parameter.

#### Jenkins Pipeline Parameters
- **BROWSER**: chrome, firefox, safari (default: chrome)
- **TEST_SUITE**: all, shopping, greenkart, amazon, flightbooking, practice (default: all)
- **HEADLESS**: true/false (default: true in CI)

#### Docker Compose Management
```bash
# Start services
docker compose up -d

# Check status
docker ps

# View logs
docker logs -f jenkins-selenium
docker logs -f selenium-chrome

# Stop services
docker compose down

# Clean everything (including volumes)
docker compose down -v
```

#### Documentation
- [JENKINS_CI_SETUP.md](JENKINS_CI_SETUP.md) - Complete Jenkins setup and configuration guide
- [QUICK_REFERENCE.md](QUICK_REFERENCE.md) - Command cheat sheet for common operations

### Browser Choice
Parameterized via `-Dbrowser=<chrome|firefox|safari>` or `config.properties`. 

**Notes:**
- Safari only supported in local execution (not in RemoteWebDriver/Grid mode)
- Chrome/Chromium recommended for CI (better stability and performance)
- Firefox support varies by platform (Intel vs ARM64)

### Allure in CI
Retain `target/screenshots` and `target/allure-results` as build artifacts. Use Allure Jenkins plugin or Docker image for report generation. The framework automatically attaches:
- Full-page screenshots on test failures
- Page source HTML for debugging
- Browser console logs
- Step-by-step execution timeline

## Cucumber BDD Examples (`cucumber` branch)

### Sample Gherkin Scenario
From `shopping.feature`:
```gherkin
@Shopping @E2E @Critical
Feature: Shopping E2E Flow
  As a customer
  I want to search, purchase products, and complete checkout
  So that I can buy items online

  Background:
    Given I am logged in via API with email "yehor_test@test.com" and password "Admin123456!"
    When I navigate to the dashboard

  Scenario Outline: Complete end-to-end shopping flow
    When I search for product "<productName>"
    And I add the product to cart
    Then the cart count should be "1"
    When I open the cart
    Then the product "<productName>" should be in the cart
    And the product price should be "<productPrice>"
    When I proceed to checkout
    And I fill checkout form with shipping country "<country>"
    And I place the order
    Then I should see the order confirmation
    And the order should contain product "<productName>"
    And the order total should be "<productPrice>"

    Examples:
      | productName  | productPrice | country        |
      | ZARA COAT 3  | $ 11500      | United States  |
```

### Key Benefits of Cucumber Branch
1. **Business-readable scenarios** - Non-technical stakeholders can understand tests
2. **Reusable steps** - Step definitions can be used across multiple scenarios
3. **Data-driven testing** - Examples tables provide parameterization without code duplication
4. **Clear separation** - Gherkin (what to test) vs Step Definitions (how to test)
5. **Better collaboration** - BAs, QAs, and Devs speak the same language

### Cucumber vs TestNG Comparison

| Aspect | TestNG (`main`) | Cucumber (`cucumber`) |
|--------|----------------|----------------------|
| Test Definition | Java methods with `@Test` | Gherkin `.feature` files |
| Readability | Code-focused | Business-focused |
| Parameterization | `@DataProvider` | `Examples:` tables |
| Execution | TestNG XML suites | Cucumber runner + tags |
| Learning Curve | Lower (Java devs) | Higher (Gherkin syntax) |
| Best For | Technical teams, API tests | Cross-functional teams, BDD |

Both approaches use the **same Page Object Model** and infrastructure, so you can switch between branches without rewriting Page Objects.

## Troubleshooting

### Application-Specific Issues
- **Amazon storefront** may show location modals; corresponding Page Objects dismiss them, but extra waits might be necessary for geo-specific deployments.
- **Safari parallel issues**: WebDriver limitations prevent concurrent sessions; fallback to sequential or dedicate Safari-only suite.
- **CDP mismatch warnings** appear until Selenium ships matching DevTools version for the installed Chrome build; safe to ignore unless debugging DevTools APIs.

### Docker/Grid Issues
- **"no matching manifest for linux/arm64/v8"**: Platform mismatch. The `docker-compose.yml` includes platform directives. Ensure you're using Docker Desktop with proper architecture support.
- **Port already in use**: Check if services are already running: `lsof -ti:8080 | xargs kill -9` to free port 8080.
- **SessionNotCreated error**: Ensure Grid containers are healthy: `docker ps` shows "Up" status and `curl http://localhost:4444/status` returns ready. Check logs: `docker logs selenium-chrome`.
- **Tests hang in Grid**: Watch via VNC (`http://localhost:7900`, password: `secret`) to see what's happening in the browser in real-time.
- **Jenkins password not found**: Wait 30-60 seconds after `docker compose up -d`, then run: `docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword`.
- **Allure Report button missing in Jenkins**: See [Allure Reporting](#allure-reporting) section for detailed setup steps.

### Common Commands
```bash
# Restart Grid containers
docker compose restart selenium-chrome

# View real-time logs
docker logs -f selenium-chrome

# Clean slate (removes all data)
docker compose down -v

# Check Grid health
curl http://localhost:4444/status
```

