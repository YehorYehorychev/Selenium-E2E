# Quick Reference - Running Tests

## Local Execution

```bash
# All tests (Chrome)
mvn clean test

# Specific browser
mvn test -Dbrowser=firefox

# Specific suite
mvn test -DsuiteXmlFile=src/test/resources/testng-shopping.xml
mvn test -DsuiteXmlFile=src/test/resources/testng-amazon.xml
mvn test -DsuiteXmlFile=src/test/resources/testng-greenkart.xml
mvn test -DsuiteXmlFile=src/test/resources/testng-flightbooking.xml
mvn test -DsuiteXmlFile=src/test/resources/testng-practice.xml

# Headless mode
mvn test -Dheadless=true

# Combined
mvn test -Dbrowser=chrome -DsuiteXmlFile=src/test/resources/testng-shopping.xml -Dheadless=true
```

---

## Docker/Grid Execution

```bash
# Start services
docker compose up -d

# Chrome Grid
mvn test -Dselenium.grid.url=http://localhost:4444/wd/hub -Dbrowser=chrome

# Firefox Grid
mvn test -Dselenium.grid.url=http://localhost:4445/wd/hub -Dbrowser=firefox

# Watch live (VNC)
open http://localhost:7900  # Chrome (password: secret)
open http://localhost:7901  # Firefox (password: secret)

# Stop services
docker compose down
```

---

## Jenkins

```bash
# Start
docker compose up -d

# Rebuild Jenkins image (if Dockerfile changed)
docker compose build --no-cache jenkins
docker compose up -d

# Get password
docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword

# Open UI
open http://localhost:8080

# Stop
docker compose down
```

### Running Specific Test Suites in Jenkins

When creating/running a Jenkins job:

1. **Click "Build with Parameters"** (not just "Build")
2. **Select TEST_SUITE parameter:**
   - `all` - Runs all tests (testng.xml)
   - `shopping` - Runs only shopping tests (testng-shopping.xml)
   - `amazon` - Runs only Amazon tests (testng-amazon.xml)
   - `greenkart` - Runs only GreenKart tests (testng-greenkart.xml)
   - `flightbooking` - Runs only flight booking tests (testng-flightbooking.xml)
   - `practice` - Runs only practice page tests (testng-practice.xml)
3. **Select BROWSER parameter:** chrome, firefox, safari
4. **Select HEADLESS parameter:** true/false

**Note:** If you click "Build" instead of "Build with Parameters", Jenkins will use default values (all tests, chrome, headless=true).

---

## Allure Reports

### Local
```bash
# Interactive viewer
mvn allure:serve

# Static report
mvn allure:report
# Report: target/site/allure-maven-plugin/index.html
```

### Jenkins
After build completes, look for **"Allure Report"** button in left sidebar (NOT in artifacts).

**If button is missing:**
1. Install "Allure Jenkins Plugin" (Manage Jenkins → Plugins)
2. Configure Allure commandline tool (Manage Jenkins → Tools)
   - **Name MUST be exactly:** `Allure`
   - **Option A (Recommended):** Use pre-installed (from Dockerfile.jenkins)
     - Installation directory: `/opt/allure-2.27.0`
     - Uncheck "Install automatically"
   - **Option B:** Choose "Install automatically" + select version `2.27.0`
3. Rebuild Jenkins if using Dockerfile: `docker compose build jenkins && docker compose up -d`
4. Re-run the job
5. Button appears after first successful report generation

**Verify report data:**
- Check archived artifacts contain `target/allure-results/*.json` files
- Look for "Generating Allure report..." in build logs

---

## Docker Commands

```bash
# Status
docker ps

# Logs
docker logs -f jenkins-selenium
docker logs -f selenium-chrome

# Restart Grid
docker compose restart selenium-chrome

# Clean all (removes volumes!)
docker compose down -v

# Grid health
curl http://localhost:4444/status
```

---

## Useful URLs

| Service | URL |
|---------|-----|
| Jenkins | http://localhost:8080 |
| Chrome Grid | http://localhost:4444/ui |
| Firefox Grid | http://localhost:4445/ui |
| Chrome VNC | http://localhost:7900 (password: `secret`) |
| Firefox VNC | http://localhost:7901 (password: `secret`) |

---

## Troubleshooting

```bash
# Port busy
lsof -ti:8080 | xargs kill -9

# Grid not responding
docker compose restart selenium-chrome
curl http://localhost:4444/status

# Jenkins password
docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword

# Clean slate
docker compose down -v
docker compose up -d
```

---

## Test Data

Fixtures: `src/test/resources/assets/data/*.json`
Dynamic: `TestDataFactory` (Faker-based)

---

## Browser Selection

Framework auto-detects environment:
- **Local**: WebDriverManager → Local browser
- **Grid**: `-Dselenium.grid.url` → RemoteWebDriver
- **CI**: Auto-detects `JENKINS_HOME` → Headless + Grid

