# Selenium Framework

## Overview
Resilient UI/API automation framework built with Java 21+, Maven, and TestNG. It drives Page Object Model coverage for multiple sample applications (GreenKart, Shopping, Amazon, Flight Booking, Practice Page, etc.) while reusing a common `BaseTest`, rich Page Objects, helper utilities (waits, screenshots, API clients, JSON readers), and Allure reporting hooks.

## Tech Stack
- Selenium 4.38.0 (WebDriver, RemoteWebDriver, DevTools, waits) orchestrated via WebDriverManager 5.9.2 for automatic binary provisioning.
- TestNG 7.11.0 as the primary runner (suite control, data providers, listeners, parallelism).
- SLF4J logging, Allure TestNG adapter 2.27.0, AShot 1.5.4 for full-page screenshots.
- Jackson 2.18.x, custom `JsonDataHelper`, and REST-assured 5.5.6 for API-driven flows (e.g., shopping authentication token).
- Faker (DataFaker 2.3.1) + Lombok (records, `@SneakyThrows`) for concise, randomized test data factories.
- Maven Surefire 3.2.5 + Allure Maven plugin for CLI/CI integration.
- Docker Compose for Jenkins + Selenium Grid CI environment.

## Project Layout
```
pom.xml
Jenkinsfile
docker-compose.yml          (STANDALONE Grid for Apple Silicon)
docker-compose-hub.yml      (Hub+Nodes alternative for Intel)
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
      └─ testng*.xml          (Multiple suite files per module)
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
- Ensure Allure Jenkins plugin is installed (Manage Jenkins → Plugins → Allure)
- Check build logs for "Generating Allure report..." message
- Verify `target/allure-results/` contains `.json` files
- Re-run the pipeline after plugin installation

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

#### Architecture
Docker Compose uses **STANDALONE Grid architecture** where each browser runs in its own isolated container with built-in Grid hub. This provides:
- ✅ Simple setup and configuration
- ✅ Independent browser isolation
- ✅ Easy scaling (add container = add browser capacity)
- ✅ Separate Grid UI per browser for better debugging

#### Quick Start with Docker Compose

**For Apple Silicon (M1/M2/M3 Macs):**
```bash
# Uses seleniarm ARM64-compatible images
docker compose up -d

# Check status
docker ps

# Access services
# Jenkins: http://localhost:8080
# Chrome Grid: http://localhost:4444/ui
# Chrome VNC: http://localhost:7900 (password: secret)
```

**Note:** Use `docker compose` (without hyphen) for Docker Compose v2. On Apple Silicon, the framework uses `seleniarm/standalone-chromium` and `seleniarm/standalone-firefox` images optimized for ARM64 architecture.

**For Intel/AMD64 systems:**
Alternative Hub+Nodes architecture available in `docker-compose-hub.yml` for centralized Grid management.

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

## Troubleshooting

### Application-Specific Issues
- **Amazon storefront** may show location modals; corresponding Page Objects dismiss them, but extra waits might be necessary for geo-specific deployments.
- **Safari parallel issues**: WebDriver limitations prevent concurrent sessions; fallback to sequential or dedicate Safari-only suite.
- **CDP mismatch warnings** appear until Selenium ships matching DevTools version for the installed Chrome build; safe to ignore unless debugging DevTools APIs.

### Docker/Grid Issues
- **"no matching manifest for linux/arm64/v8"**: You're on Apple Silicon (M1/M2/M3). The `docker-compose.yml` uses ARM64-compatible `seleniarm` images. Use `docker compose up -d` (v2 CLI).
- **Port already in use**: Check if Jenkins/Grid is already running: `lsof -ti:8080 | xargs kill -9` to free port 8080.
- **SessionNotCreated error**: Ensure Grid containers are healthy: `docker ps` and `curl http://localhost:4444/status`. Check logs: `docker logs selenium-chrome`.
- **Tests hang in Grid**: Watch via VNC (`http://localhost:7900`, password: `secret`) to see what's happening in the browser.
- **Jenkins password not found**: Wait 30-60 seconds after `docker compose up -d`, then run: `docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword`.

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

