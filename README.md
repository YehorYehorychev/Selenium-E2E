# Selenium X Cucumber Framework 2026

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

## ⚡ Framework Highlights

### Core Features
✅ **Java 21** + **Maven** + **Selenium 4.38**  
✅ **Comprehensive Page Object Model** for 5+ applications  
✅ **Cucumber 7 BDD** (cucumber branch) with Gherkin scenarios  
✅ **Smart Wait Strategies** - No `Thread.sleep()`, only intelligent waits  
✅ **Full-Page Screenshots** (AShot) auto-attached to Allure  
✅ **API + UI Hybrid** tests (REST-assured for auth, UI for actions)  
✅ **Faker Data Generation** - Dynamic, realistic test data  
✅ **Docker Compose** - Jenkins + Selenium Grid (Chrome/Firefox)  
✅ **CI/CD Ready** - Jenkinsfile with parallel execution  
✅ **Allure Reports** - Rich HTML dashboards with screenshots & timelines

### Test Coverage
- **5 Applications**: Shopping, GreenKart, Amazon, Flight Booking, Practice Page
- **27 Scenarios** (Cucumber branch): Login, Search, Cart, Checkout, Alerts, Forms
- **22/27 passing** (81%) - 2 expected failures (broken link test, Amazon modal timing)
- **Parallel Execution** - 4 threads, ~30-35 seconds for full suite

---

## 🥒 Cucumber Branch (BDD) - **You Are Here**

### What Makes It Special
```gherkin
@Shopping @Critical
Scenario: Complete checkout
  Given I am logged in via API
  When I add "ZARA COAT 3" to cart
  And I proceed to checkout
  Then order confirmation should appear
```

**vs Traditional TestNG:**
```java
@Test
public void shouldCompleteCheckout() {
    login(); addToCart("ZARA COAT 3"); checkout(); verifyOrder();
}
```

### Architecture
```
Feature Files (Gherkin)  →  Step Definitions (Java)  →  Page Objects  →  Selenium
      ↓                           ↓                          ↓
  WHAT to test            HOW to map steps          HOW to implement
```

### Cucumber-Specific Components
- **`CucumberTestRunner.java`** - TestNG + Cucumber integration
- **`CucumberHooks.java`** - WebDriver lifecycle (@Before/@After)
- **`ScenarioContext.java`** - Share state between steps
- **`*.feature` files** - Business-readable test scenarios
- **Step definitions** - Gherkin → Java mapping

**No `BaseTest.java`** in Cucumber branch - lifecycle handled by hooks!

---

## 📁 Project Structure

### 🥒 Cucumber Branch (BDD) - **Current**

```
src/main/java/com/yehorychev/selenium/
├── config/ConfigProperties.java           # Centralized config
├── helpers/                               # Reusable utilities
│   ├── WaitHelper.java                   # Smart waits (no Thread.sleep!)
│   ├── ScreenshotHelper.java             # Full-page screenshots (AShot)
│   └── JsonDataHelper.java               # JSON test data loader
└── pages/                                 # Page Object Model (shared)
    ├── common/BasePage.java              # Base with reusable methods
    ├── amazon/, flightbooking/, greenkart/, practice/, shopping/

src/test/java/com/yehorychev/selenium/
├── runner/CucumberTestRunner.java         # TestNG + Cucumber entry point
├── context/ScenarioContext.java           # Share state between steps
├── hooks/CucumberHooks.java               # WebDriver lifecycle (@Before/@After)
├── steps/                                 # Step definitions (Gherkin → Java)
│   ├── amazon/AmazonSteps.java
│   ├── flightbooking/FlightBookingSteps.java
│   ├── greenkart/GreenKartSteps.java
│   ├── practice/PracticeSteps.java
│   └── shopping/{LoginSteps, DashboardSteps, CartSteps, CheckoutSteps}
├── helpers/shopping/ShoppingApiAuthClient # REST-assured API client
└── utils/TestDataFactory.java             # Faker-based data generation

src/test/resources/
├── features/*.feature                     # Gherkin BDD scenarios ⭐
├── assets/data/*.json                     # Static test fixtures
├── config.properties                      # Environment config
└── testng-cucumber.xml                    # TestNG suite

docker-compose.yml, Jenkinsfile            # CI/CD infrastructure (shared)
```

### 🧪 TestNG Branch (Traditional) - `main`

```
src/main/java/com/yehorychev/selenium/
└── (Same as Cucumber: config, helpers, pages) ✅

src/test/java/com/yehorychev/selenium/
├── core/                                  # Base test infrastructure
│   ├── BaseTest.java                     # WebDriver setup/teardown
│   └── ShoppingAuthenticatedBaseTest.java # Pre-authenticated base
├── listeners/AllureTestListener.java      # TestNG listener for Allure
└── tests/                                 # Test classes ⭐
    ├── amazon/ui/{AmazonTests.java}
    ├── flightbooking/ui/{FlightBookingTests.java}
    ├── greenkart/ui/{GreenKartTests.java}
    ├── practice/ui/{AlertTests.java}
    └── shopping/
        ├── api/ShoppingApiLoginTest.java
        ├── data/ShoppingDataProviders.java # @DataProvider methods
        └── ui/{ShoppingLoginTest, ShoppingProductsTest, ...}

src/test/resources/
├── assets/data/*.json                     # Static test fixtures
├── config.properties                      # Environment config
└── testng-*.xml                           # Multiple suite files per module ⭐

docker-compose.yml, Jenkinsfile            # CI/CD infrastructure (shared)
```

### 🔄 Key Differences: Cucumber vs TestNG

| Component | TestNG (`main`) | Cucumber (`cucumber`) |
|-----------|----------------|----------------------|
| **Tests** | `@Test` methods in Java classes | `.feature` files (Gherkin) |
| **Data** | `@DataProvider` methods | `Examples:` tables |
| **Lifecycle** | `BaseTest.java` | `CucumberHooks.java` |
| **Execution** | Multiple `testng.xml` suites | Single runner + tags |
| **State** | Test class instance variables | `ScenarioContext` shared object |
| **Organization** | `/tests/module/ui/` hierarchy | `/steps/module/` + `/features/` |

---

## ⚙️ Configuration

`config.properties` - single source of truth:
```properties
# Base URLs per application
base.url.shopping=https://rahulshettyacademy.com/client/
base.url.amazon=https://www.amazon.com/

# Browser
browser.default=chrome
browser.headless.enabled=true

# Waits
wait.default.seconds=5
wait.polling.millis=200

# Screenshots
screenshot.directory=src/test/resources/assets/screenshots
screenshot.failures.only=true

# Test credentials (override with -D flags or env vars)
shopping.username=yehor_test@test.com
shopping.password=Admin123456!
```

**Runtime overrides:**
```bash
mvn test -Dbrowser=firefox -Dheadless=false -Dbase.url.shopping=https://staging.example.com
```

---

## 📊 Test Data Strategy

1. **Static fixtures** - JSON files (`assets/data/*.json`)
   - Product catalogs, passenger lists, alert texts
   - Loaded via `JsonDataHelper`

2. **Dynamic data** - Faker-generated via `TestDataFactory`
   - Random names, emails, credit cards, addresses
   - Keeps tests idempotent yet realistic

3. **Hybrid** - API + Faker for Shopping tests
   - API auth token (REST-assured) → UI session
   - Faker generates customer details for checkout

---

## 🚀 Running Tests (Cucumber Branch)

### Full Suite
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-cucumber.xml
```

### By Tags
```bash
# Shopping module only
mvn test -Dcucumber.filter.tags="@Shopping"

# Critical tests
mvn test -Dcucumber.filter.tags="@Critical"

# Combine tags
mvn test -Dcucumber.filter.tags="@Shopping and @Smoke"
mvn test -Dcucumber.filter.tags="@Critical and not @Broken"
```

### Single Feature
```bash
mvn test -Dcucumber.features=src/test/resources/features/shopping.feature
```

### Runtime Overrides
```bash
# Firefox headless
mvn test -Dbrowser=firefox -Dheadless=true

# Chrome with Selenium Grid
mvn test -Dbrowser=chrome -Dselenium.grid.url=http://localhost:4444/wd/hub

# Custom config
mvn test -Dbase.url.shopping=https://staging.example.com
```

### Parallel Execution
Controlled by `testng-cucumber.xml`:
```xml
<suite name="Cucumber Test Suite" parallel="methods" thread-count="4">
```

**Features:**
- ✅ Scenario-level parallelism (via `@DataProvider(parallel = true)`)
- ✅ Shared WebDriver pool per thread
- ✅ Isolated `ScenarioContext` per scenario
- ✅ Screenshots on failures
- ✅ Allure attachments auto-generated

---

## 📊 Allure Reporting

### Local Report Generation
```bash
# Interactive viewer (auto-refreshes)
mvn allure:serve

# Static HTML report
mvn allure:report
# Open: target/site/allure-maven-plugin/index.html
```

### Jenkins Integration
**When tests run in Jenkins:**
1. Tests populate `target/allure-results/` with JSON artifacts
2. Allure Jenkins plugin auto-generates HTML report
3. **"Allure Report"** button appears in build sidebar
4. Interactive dashboard with:
   - Test execution trends
   - Full-page screenshots on failures
   - Step-by-step timeline
   - Browser logs & page source
   - Tag/severity filtering

### Setup Jenkins Allure

1. **Install Plugin:** Manage Jenkins → Plugins → "Allure Jenkins Plugin"

2. **Configure Tool:** Manage Jenkins → Tools → Allure Commandline
   - Name: `Allure` (must match Jenkinsfile)
   - ✅ Pre-installed in Docker: `/opt/allure-2.27.0`
   - Or check "Install automatically" for auto-download

3. **Rebuild if needed:**
   ```bash
   docker compose build --no-cache jenkins
   docker compose up -d
   ```

4. **Verify:** Build job → Check for "Allure Report" button in sidebar

### Cucumber + Allure Integration
```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-cucumber7-jvm</artifactId>
    <version>2.32.0</version>
</dependency>
```

```java
// CucumberTestRunner.java
@CucumberOptions(
    plugin = {"io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"}
)
```

**Auto-attached to reports:**
- ✅ Full-page screenshots (failures)
- ✅ Page source HTML
- ✅ Browser console logs
- ✅ Gherkin steps timeline
- ✅ API request/response (REST-assured)

---

## 🐳 CI/CD with Docker & Jenkins

### Architecture
```
Docker Compose orchestrates 3 services:
├── Jenkins (CI/CD server)
├── Chrome Grid (Selenium standalone)
└── Firefox Grid (Selenium standalone)
```

**Features:**
- ✅ Apple Silicon (M1/M2/M3) support via `seleniarm` images
- ✅ Built-in VNC servers for live test watching
- ✅ Separate Grid UI per browser
- ✅ Jenkins pre-configured with Java 21 + Maven + Allure

### Quick Start
```bash
# Start all services
docker compose up -d

# Check status
docker ps

# Access services:
# Jenkins:      http://localhost:8080
# Chrome Grid:  http://localhost:4444/ui
# Firefox Grid: http://localhost:4445/ui
# Chrome VNC:   http://localhost:7900 (password: secret)
# Firefox VNC:  http://localhost:7901 (password: secret)

# Get Jenkins initial password
docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword
```

### Running Tests via Grid
```bash
# Local → Chrome Grid
mvn test -Dselenium.grid.url=http://localhost:4444/wd/hub -Dbrowser=chrome

# Local → Firefox Grid
mvn test -Dselenium.grid.url=http://localhost:4445/wd/hub -Dbrowser=firefox

# Jenkins (automatic via Jenkinsfile)
# Grid URL selected based on BROWSER parameter
```

### Jenkinsfile Features
```groovy
parameters {
    choice(name: 'BROWSER', choices: ['chrome', 'firefox'])
    booleanParam(name: 'HEADLESS', defaultValue: true)
}
```

**Auto-detects CI environment:**
- ✅ Forces headless in CI (`JENKINS_HOME` env var)
- ✅ Applies Docker-safe flags (`--no-sandbox`, `--disable-dev-shm-usage`)
- ✅ Switches to RemoteWebDriver when Grid URL set
- ✅ Generates Allure report
- ✅ Archives screenshots & test results

### Management Commands
```bash
# View logs
docker logs -f selenium-chrome

# Restart service
docker compose restart jenkins

# Clean slate
docker compose down -v

# Check Grid health
curl http://localhost:4444/status
```

---

## 🥒 Cucumber BDD Examples

### Gherkin Scenario
```gherkin
@Shopping @E2E @Critical
Feature: Shopping E2E Flow

  Scenario Outline: Complete checkout process
    Given the shopping application is opened
    And I am logged in via API with email "<email>" and password "<password>"
    When I navigate to the dashboard
    And I search for product "<productName>"
    And I add the product to cart
    And I open the cart
    Then the product "<productName>" should be in the cart
    And the product price should be "<productPrice>"
    When I proceed to checkout
    And I fill checkout form with shipping country "<country>"
    And I place the order
    Then I should see the order confirmation
    And the order should contain product "<productName>"
    And the order total should be "<productPrice>"

    Examples:
      | email              | password      | productName | productPrice | country        |
      | yehor_test@test.com| Admin123456!  | ZARA COAT 3 | $ 11500      | United States  |
```

### Step Definition
```java
@When("I open the cart")
public void openCart() {
    logger.info("Opening shopping cart");
    DashboardPage dashboard = context.get("dashboardPage", DashboardPage.class);
    CartPage cartPage = dashboard.openCart();
    context.set("cartPage", cartPage);
}

@Then("the product {string} should be in the cart")
public void verifyProductInCart(String expectedProductName) {
    CartPage cartPage = context.get("cartPage", CartPage.class);
    String actualProductName = cartPage.getFirstProductName();
    assertEquals(actualProductName, expectedProductName);
}
```

### Architecture Flow
```
Feature File → Step Definition → ScenarioContext → Page Object → Selenium
```

### Key Benefits
1. **Business-readable** - Non-technical stakeholders can understand tests
2. **Reusable steps** - Same step definition across multiple scenarios
3. **Data-driven** - Examples tables for parameterization
4. **Living documentation** - Feature files = project documentation
5. **Tag-based execution** - Run by priority, module, or status

### Cucumber vs TestNG

| Aspect | TestNG (`main`) | Cucumber (`cucumber`) |
|--------|----------------|----------------------|
| Test Definition | Java methods with `@Test` | Gherkin `.feature` files |
| Readability | Code-focused | Business-focused |
| Parameterization | `@DataProvider` | `Examples:` tables |
| Execution | TestNG XML suites | Tags + runner |
| State | Instance variables | ScenarioContext |
| Lifecycle | `BaseTest.java` | `CucumberHooks.java` |

---

## 🐛 Troubleshooting

### Common Issues
- **Amazon modals** - Location/preferences popups may appear; Page Objects dismiss them but timing-dependent
- **Safari parallel** - WebDriver doesn't support concurrent Safari sessions; run sequentially
- **Grid SessionNotCreated** - Check Grid health: `curl http://localhost:4444/status` and `docker logs selenium-chrome`
- **Tests hang** - Watch via VNC: `http://localhost:7900` (password: `secret`)
- **Jenkins password** - Wait 30s after startup, then: `docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword`
- **Allure button missing** - See [Allure Reporting](#📊-allure-reporting) section

### Quick Fixes
```bash
# Restart Grid
docker compose restart selenium-chrome

# View logs
docker logs -f selenium-chrome

# Clean slate
docker compose down -v

# Check Grid health
curl http://localhost:4444/status
```

---

## 📈 Test Coverage

| Module | Scenarios | Status | Notes |
|--------|-----------|--------|-------|
| **Shopping** | 4 | ✅ 100% | Login (UI/API), Cart, Checkout E2E |
| **GreenKart** | 4 | ✅ 100% | Vegetables, promo, sorting |
| **FlightBooking** | 4 | ✅ 100% | Currency, passengers, search |
| **Practice** | 2 | ✅ Pass | Alerts, scroll, tables |
| **Practice Links** | 1 | ⚠️ Expected Fail | Broken link validation |
| **Amazon** | 4 | ⚠️ 50% | Search works, modal timing issues |

**Overall: 22/27 scenarios (81%)** | **Execution: ~30-35s** (4 parallel threads)

---

---

## 🤝 Contributing

### Adding New Tests

1. **Feature File** (`src/test/resources/features/`)
   ```gherkin
   @NewModule @Smoke
   Feature: New functionality
     Scenario: Test something
       Given precondition
       When action
       Then expected result
   ```

2. **Step Definitions** (`src/test/java/.../steps/`)
   ```java
   @Given("precondition")
   public void setup() { /* use Page Objects */ }
   ```

3. **Page Objects** (`src/main/java/.../pages/`) - reuse existing or create new

4. **Configuration** (`config.properties`) - add URLs/credentials if needed

5. **Run:** `mvn test -Dcucumber.filter.tags="@NewModule"`

### Code Quality Guidelines
- ✅ Use Page Object Model - no Selenium in steps
- ✅ Use `WaitHelper` - no `Thread.sleep()`
- ✅ Descriptive Gherkin - read like documentation
- ✅ Reuse step definitions - DRY principle
- ✅ Tag scenarios - `@Smoke`, `@Critical`, `@Module`

---

## 📄 License

Educational project showcasing Selenium + Cucumber + Docker + Jenkins with industry best practices.

**Framework Version**: 1.0-SNAPSHOT  
**Last Updated**: December 2025  
**Branch**: `cucumber` (BDD with Gherkin scenarios)
