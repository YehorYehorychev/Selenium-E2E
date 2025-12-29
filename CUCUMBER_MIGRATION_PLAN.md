# Cucumber Migration Plan

## 🎯 Goal
Migrate the entire Selenium framework from pure TestNG to Cucumber BDD while maintaining all existing functionality and following industry best practices.

## 📋 Current Framework Structure
- **Test Framework**: TestNG
- **Page Object Model**: ✅ Implemented
- **Reporting**: Allure Reports
- **CI/CD**: Jenkins with Docker Compose
- **Browsers**: Chrome, Firefox (Selenium Grid)
- **Test Modules**: Amazon, FlightBooking, GreenKart, Practice, Shopping

---

## 🚀 Migration Strategy

### Phase 1: Setup & Dependencies ✅
**Goal**: Add Cucumber dependencies and configure the project

**Tasks**:
1. ✅ Add Cucumber dependencies to `pom.xml`:
   - `cucumber-java`
   - `cucumber-testng`
   - `cucumber-picocontainer` (for dependency injection)
2. ✅ Configure Cucumber plugins for Allure integration
3. ✅ Create folder structure for features and step definitions

**File Changes**:
- `pom.xml` - add dependencies
- Create `src/test/resources/features/` directory
- Create `src/test/java/com/yehorychev/selenium/steps/` directory

---

### Phase 2: Core Infrastructure
**Goal**: Build reusable Cucumber infrastructure

**Tasks**:
1. Create `CucumberHooks` class for setup/teardown
   - Replace `@BeforeMethod` and `@AfterMethod` from BaseTest
   - Manage WebDriver lifecycle per scenario
   - Integrate screenshot capture on failure
   - Add Allure attachments

2. Create `CucumberTestRunner` class
   - Configure TestNG runner for Cucumber
   - Set Allure plugin
   - Configure tags, glue packages, features path

3. Create `ScenarioContext` class
   - Share data between steps within a scenario
   - Store test data, page objects, etc.

4. Update `BaseTest` or create `CucumberBaseSteps`
   - Provide common step utilities
   - Access to WebDriver, WaitHelper, etc.

**New Files**:
- `src/test/java/com/yehorychev/selenium/hooks/CucumberHooks.java`
- `src/test/java/com/yehorychev/selenium/runner/CucumberTestRunner.java`
- `src/test/java/com/yehorychev/selenium/context/ScenarioContext.java`
- `src/test/java/com/yehorychev/selenium/steps/common/CommonSteps.java`

---

### Phase 3: Feature Files Creation
**Goal**: Convert TestNG tests to Gherkin scenarios

**Approach**: One module at a time, starting with simplest

#### 3.1 Shopping Module (Start Here - Most Complete)
- **File**: `src/test/resources/features/shopping.feature`
- **Scenarios**:
  - Login via UI
  - Login via API and verify Dashboard
  - Add product to cart (E2E flow)
  - Complete checkout with payment

#### 3.2 GreenKart Module
- **File**: `src/test/resources/features/greenkart.feature`
- **Scenarios**:
  - Search and add vegetables
  - Verify cart totals
  - Apply promo code
  - Navigate to Top Deals

#### 3.3 FlightBooking Module
- **File**: `src/test/resources/features/flightbooking.feature`
- **Scenarios**:
  - Select passengers from dropdown
  - Select country with autocomplete
  - Check/uncheck checkboxes

#### 3.4 Practice Module
- **File**: `src/test/resources/features/practice.feature`
- **Scenarios**:
  - Handle alerts
  - Validate broken links
  - Mouse hover actions

#### 3.5 Amazon Module
- **File**: `src/test/resources/features/amazon.feature`
- **Scenarios**:
  - Search products
  - Verify search results
  - Navigate categories

**Feature File Template**:
```gherkin
@Shopping @E2E
Feature: Shopping E2E Flow
  As a customer
  I want to browse and purchase products
  So that I can complete an order

  Background:
    Given the shopping application is opened

  @Login @UI
  Scenario: Login with valid credentials via UI
    When I login with email "yehor_test@test.com" and password "Admin123456!"
    Then I should see the dashboard page
    And the "Sign Out" button should be visible

  @AddToCart @Smoke
  Scenario: Add product to cart
    Given I am logged in via API
    When I search for product "ZARA COAT 3"
    And I add the product to cart
    Then the cart count should be "1"
```

---

### Phase 4: Step Definitions
**Goal**: Implement step definition classes

**Organization**:
```
src/test/java/com/yehorychev/selenium/steps/
├── common/
│   └── CommonSteps.java         # Shared steps (navigation, waits)
├── shopping/
│   ├── ShoppingLoginSteps.java
│   ├── ShoppingDashboardSteps.java
│   ├── ShoppingCartSteps.java
│   └── ShoppingCheckoutSteps.java
├── greenkart/
│   ├── GreenKartSearchSteps.java
│   └── GreenKartCartSteps.java
├── flightbooking/
│   └── FlightBookingSteps.java
├── practice/
│   └── PracticeSteps.java
└── amazon/
    └── AmazonSteps.java
```

**Step Definition Template**:
```java
public class ShoppingLoginSteps {
    private final ScenarioContext context;
    private final WebDriver driver;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    public ShoppingLoginSteps(ScenarioContext context) {
        this.context = context;
        this.driver = context.getDriver();
    }

    @Given("the shopping application is opened")
    public void openShoppingApp() {
        String url = ConfigProperties.getProperty("base.url.shopping");
        driver.get(url);
        loginPage = new LoginPage(driver);
    }

    @When("I login with email {string} and password {string}")
    public void loginWithCredentials(String email, String password) {
        loginPage.login(email, password);
        dashboardPage = new DashboardPage(driver);
    }

    @Then("I should see the dashboard page")
    public void verifyDashboardPage() {
        assertTrue(dashboardPage.isDisplayed());
    }
}
```

---

### Phase 5: Data-Driven Testing
**Goal**: Use Scenario Outline for parameterized tests

**Examples**:
- Multiple search keywords (Amazon)
- Different products (Shopping)
- Various passenger counts (FlightBooking)

**Template**:
```gherkin
@DataDriven
Scenario Outline: Search for multiple products
  Given I am on the shopping page
  When I search for "<product>"
  Then I should see results for "<product>"

  Examples:
    | product       |
    | ZARA COAT 3   |
    | ADIDAS ORIGINAL |
    | IPHONE 13 PRO |
```

---

### Phase 6: API Steps Integration
**Goal**: Reuse existing API helpers in Cucumber steps

**Tasks**:
1. Keep `ShoppingApiAuthClient` as-is
2. Create API-specific steps:
   - `@Given I am logged in via API`
   - Use API client to get token
   - Inject token/cookies into WebDriver

**Example**:
```java
@Given("I am logged in via API")
public void loginViaAPI() {
    ShoppingApiAuthClient apiClient = new ShoppingApiAuthClient();
    String token = apiClient.getAuthToken(email, password);
    String userId = apiClient.getUserId();
    
    // Inject into browser
    driver.get(ConfigProperties.getProperty("base.url.shopping"));
    // Set localStorage/cookies
    context.set("authToken", token);
    context.set("userId", userId);
}
```

---

### Phase 7: Reporting Integration
**Goal**: Ensure Allure works seamlessly with Cucumber

**Tasks**:
1. Add `@Step` annotations where needed (optional - Cucumber auto-reports)
2. Attach screenshots to Allure on failure (via Hooks)
3. Attach logs, page source, cookies
4. Configure severity and descriptions via tags

**Allure Annotations**:
```java
@Attachment(value = "Screenshot on Failure", type = "image/png")
public byte[] captureScreenshot() {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
}
```

---

### Phase 8: TestNG XML & Maven Configuration
**Goal**: Update test execution configuration

**Changes**:
1. Create `testng-cucumber.xml`:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Cucumber Test Suite" parallel="classes" thread-count="4">
    <test name="Cucumber Tests">
        <classes>
            <class name="com.yehorychev.selenium.runner.CucumberTestRunner"/>
        </classes>
    </test>
</suite>
```

2. Update `Jenkinsfile` to support running Cucumber tests
3. Add Maven Surefire configuration for Cucumber

---

### Phase 9: Tags Strategy
**Goal**: Organize tests with Cucumber tags

**Tag Categories**:
- **By Module**: `@Shopping`, `@Amazon`, `@GreenKart`, `@FlightBooking`, `@Practice`
- **By Type**: `@UI`, `@API`, `@E2E`, `@Smoke`, `@Regression`
- **By Priority**: `@Critical`, `@High`, `@Medium`, `@Low`
- **By Status**: `@WIP`, `@Bug`, `@Ignore`

**Usage**:
```bash
# Run only shopping smoke tests
mvn test -Dcucumber.filter.tags="@Shopping and @Smoke"

# Run all except WIP
mvn test -Dcucumber.filter.tags="not @WIP"
```

---

### Phase 10: Parallel Execution
**Goal**: Run scenarios in parallel

**Options**:
1. **TestNG Parallel**: Use `parallel="classes"` in testng.xml
2. **Maven Surefire**: Configure forkCount
3. **Cucumber Native**: Use `cucumber.execution.parallel.enabled=true`

**Recommended**: TestNG parallel with thread-safe WebDriver management

---

### Phase 11: Migration Verification
**Goal**: Ensure feature parity with old tests

**Checklist**:
- [ ] All test scenarios migrated
- [ ] All assertions preserved
- [ ] Screenshots captured on failure
- [ ] Allure reports generated
- [ ] Jenkins pipeline runs successfully
- [ ] Parallel execution works
- [ ] Data-driven tests work
- [ ] API + UI hybrid scenarios work

---

### Phase 12: Cleanup & Documentation
**Goal**: Finalize the migration

**Tasks**:
1. Update `README.md` with Cucumber usage
2. Add `CUCUMBER_GUIDE.md` with examples
3. Remove old TestNG test classes (keep in main branch)
4. Update Jenkins jobs
5. Add Cucumber best practices doc

---

## 📂 Final Folder Structure

```
selenium-framework/
├── src/
│   ├── main/java/com/yehorychev/selenium/
│   │   ├── helpers/           # WaitHelper, ScreenshotHelper
│   │   ├── pages/             # Page Objects (unchanged)
│   │   └── utils/             # ConfigProperties, etc.
│   └── test/
│       ├── java/com/yehorychev/selenium/
│       │   ├── hooks/
│       │   │   └── CucumberHooks.java
│       │   ├── runner/
│       │   │   └── CucumberTestRunner.java
│       │   ├── context/
│       │   │   └── ScenarioContext.java
│       │   ├── steps/
│       │   │   ├── common/
│       │   │   ├── shopping/
│       │   │   ├── greenkart/
│       │   │   ├── flightbooking/
│       │   │   ├── practice/
│       │   │   └── amazon/
│       │   └── support/       # TestDataFactory, API helpers
│       └── resources/
│           ├── features/
│           │   ├── shopping.feature
│           │   ├── greenkart.feature
│           │   ├── flightbooking.feature
│           │   ├── practice.feature
│           │   └── amazon.feature
│           ├── config.properties
│           ├── testng-cucumber.xml
│           └── cucumber.properties
```

---

## ✅ Success Criteria

1. **Zero Test Loss**: All existing test coverage preserved
2. **Readable Tests**: Non-technical stakeholders can read features
3. **Maintainability**: Step definitions reuse Page Objects
4. **Performance**: Parallel execution works without flakiness
5. **Reporting**: Allure reports show scenarios, steps, and attachments
6. **CI/CD**: Jenkins runs Cucumber suite successfully
7. **Flexibility**: Can run by tags, features, or scenarios

---

## 🎯 Migration Order (Recommended)

1. ✅ **Phase 1**: Dependencies & Setup
2. ✅ **Phase 2**: Core Infrastructure (Hooks, Runner, Context)
3. 🔄 **Phase 3.1**: Shopping Module (simplest, most complete)
4. 🔄 **Phase 4.1**: Shopping Step Definitions
5. **Phase 3.2-3.5**: Remaining modules (one by one)
6. **Phase 5**: Data-Driven scenarios
7. **Phase 6**: API integration verification
8. **Phase 7**: Allure reporting verification
9. **Phase 8**: TestNG/Maven configuration
10. **Phase 9**: Tags implementation
11. **Phase 10**: Parallel execution tuning
12. **Phase 11**: Full verification
13. **Phase 12**: Documentation & cleanup

---

## 🔧 Commands Reference

```bash
# Run all Cucumber tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng-cucumber.xml

# Run specific tags
mvn test -Dcucumber.filter.tags="@Shopping and @Smoke"

# Generate Allure report
allure serve target/allure-results

# Run in Jenkins
# (Use cucumber parameter in Jenkinsfile)
```

---

## 📚 Resources
- [Cucumber Best Practices](https://cucumber.io/docs/guides/10-minute-tutorial/)
- [Cucumber-JVM GitHub](https://github.com/cucumber/cucumber-jvm)
- [Allure Cucumber Integration](https://docs.qameta.io/allure/#_cucumber_jvm)

---

**Next Step**: Start with Phase 1 - Add Cucumber dependencies to `pom.xml`

