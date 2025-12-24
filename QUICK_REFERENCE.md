# Quick Reference - Running Tests

## Local Execution

```bash
# All tests (Chrome)
mvn clean test

# Specific browser
mvn test -Dbrowser=firefox

# Specific suite
mvn test -DsuiteXmlFile=testng-shopping.xml

# Headless mode
mvn test -Dheadless=true

# Combined
mvn test -Dbrowser=chrome -DsuiteXmlFile=testng-shopping.xml -Dheadless=true
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

# Get password
docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword

# Open UI
open http://localhost:8080

# Stop
docker compose down
```

---

## Allure Reports

```bash
# Interactive viewer
mvn allure:serve

# Static report
mvn allure:report
# Report: target/site/allure-maven-plugin/index.html
```

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

