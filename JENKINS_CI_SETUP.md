# Jenkins CI/CD Setup Guide

## Quick Start (TL;DR)

```bash
# 1. Start services
docker compose up -d

# 2. Get Jenkins password
docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword

# 3. Open Jenkins
open http://localhost:8080

# 4. Configure and run!
```

---

## Prerequisites

- Docker Desktop installed and running
- 4GB RAM available
- Ports 8080, 4444, 4445, 7900, 7901 free

---

## Step-by-Step Setup

### 1. Start Services

```bash
cd /Users/yehor/IdeaProjects/selenium-framework

# Start Jenkins + Selenium Grid containers
docker compose up -d

# Wait 30-60 seconds for initialization
```

### 2. Verify Services

```bash
docker ps
```

Should show 3 containers:
- `jenkins-selenium` (port 8080) - CI/CD server
- `selenium-chrome` (port 4444) - Chrome Grid
- `selenium-firefox` (port 4445) - Firefox Grid

### 3. Get Initial Password

```bash
# Wait 30 seconds, then:
docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword
```

Copy the password.

### 4. Open Jenkins

```bash
open http://localhost:8080
```

Or navigate to: http://localhost:8080

### 5. Initial Setup

1. **Unlock Jenkins**
   - Paste the password from step 3
   - Click "Continue"

2. **Install Plugins**
   - Choose "Install suggested plugins"
   - Wait 2-3 minutes

3. **Create Admin User**
   - Username: `admin`
   - Password: (your choice)
   - Email: your email
   - Click "Save and Continue"

4. **Jenkins URL**
   - Keep default: `http://localhost:8080/`
   - Click "Save and Finish"

### 6. Configure Tools

#### Maven
1. `Manage Jenkins` → `Tools`
2. Scroll to **Maven installations**
3. Click "Add Maven"
   - Name: `Maven3`
   - ✅ Install automatically
   - Version: `3.9.9`
4. Click "Save"

#### JDK
1. Same page, scroll to **JDK installations**
2. Click "Add JDK"
   - Name: `JDK21`
   - ✅ Install automatically from adoptium.net
   - Version: `jdk-21.0.5+11`
3. Click "Save"

### 7. Install Allure Plugin

1. `Manage Jenkins` → `Plugins`
2. Click "Available plugins"
3. Search: `Allure`
4. Check the box
5. Click "Install"
6. Wait and restart if prompted

### 8. Configure Allure Commandline Tool

**Important:** The Allure plugin requires the Allure commandline tool to generate reports.

#### Option A: Use Pre-installed Allure (Recommended)

The custom `Dockerfile.jenkins` includes Allure 2.27.0 pre-installed.

1. `Manage Jenkins` → `Tools`
2. Scroll to **Allure Commandline installations**
3. Click "Add Allure Commandline"
4. Configure:
   - **Name:** `Allure` ⚠️ (must match exactly - used in Jenkinsfile)
   - **Installation directory:** `/opt/allure-2.27.0`
   - **Uncheck** "Install automatically" (already in Docker image)
5. Click "Save"

#### Option B: Auto-Install (Alternative)

If you're NOT using the custom Dockerfile:

1. `Manage Jenkins` → `Tools`
2. Scroll to **Allure Commandline installations**
3. Click "Add Allure Commandline"
4. Configure:
   - **Name:** `Allure` ⚠️ (must match exactly)
   - **Check** "Install automatically"
   - **Version:** Select `2.27.0` or latest from dropdown
5. Click "Save"

**Verification:**
- After next build, "Allure Report" button should appear in build sidebar
- Check build logs for: `Generating Allure report...`

### 9. Create Pipeline Job

1. Main page → "New Item"
2. Name: `Selenium_E2E`
3. Type: **Pipeline**
4. Click "OK"

#### Configure Parameters

In **General** section:
- ✅ "This project is parameterized"
- Add 3 Choice Parameters:

**Parameter 1: BROWSER**
- Name: `BROWSER`
- Choices (one per line):
  ```
  chrome
  firefox
  safari
  ```

**Parameter 2: TEST_SUITE**
- Name: `TEST_SUITE`
- Choices:
  ```
  all
  shopping
  greenkart
  amazon
  flightbooking
  practice
  ```

**Parameter 3: HEADLESS**
- Name: `HEADLESS`
- Choices:
  ```
  true
  false
  ```

#### Configure Pipeline

Scroll to **Pipeline** section:
- Definition: **Pipeline script from SCM**
- SCM: **Git**
- Repository URL: `https://github.com/YehorYehorychev/Selenium-E2E.git`
- Branch: `*/main`
- Script Path: `Jenkinsfile`

Click "Save"

### 10. Run First Build

1. Click "Build with Parameters"
2. Select:
   - BROWSER: `chrome`
   - TEST_SUITE: `shopping`
   - HEADLESS: `true`
3. Click "Build"

### 11. Monitor Execution

**Console Output:**
- Click on build #1
- Click "Console Output"
- Watch logs in real-time

**VNC Viewer:**
```bash
open http://localhost:7900
```
- Password: `secret`
- Watch browser automation live!

**Allure Report:**
- After build completes
- Click "Allure Report" in left menu

---

## Services & URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| Jenkins | http://localhost:8080 | admin / (your password) |
| Chrome Grid | http://localhost:4444/ui | - |
| Firefox Grid | http://localhost:4445/ui | - |
| Chrome VNC | http://localhost:7900 | password: `secret` |
| Firefox VNC | http://localhost:7901 | password: `secret` |

---

## Common Commands

```bash
# Start
docker compose up -d

# Status
docker ps

# Logs
docker logs -f jenkins-selenium
docker logs -f selenium-chrome

# Stop
docker compose down

# Clean all (removes data!)
docker compose down -v

# Grid health
curl http://localhost:4444/status

# Jenkins password
docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword
```

---

## Troubleshooting

### Port 8080 already in use
```bash
lsof -ti:8080 | xargs kill -9
```

### Can't connect to Grid
```bash
# Check containers
docker ps

# Check Grid health
curl http://localhost:4444/status

# Restart Grid
docker compose restart selenium-chrome
```

### Jenkins password not working
```bash
# Clean start
docker compose down -v
docker compose up -d
# Wait 60 seconds
docker exec jenkins-selenium cat /var/jenkins_home/secrets/initialAdminPassword
```

### Tests fail with SessionNotCreated
- Ensure Grid containers are running: `docker ps`
- Check logs: `docker logs selenium-chrome`
- Verify Grid status: http://localhost:4444/ui

---

## Architecture

```
┌──────────────┐         ┌──────────────────┐
│   Jenkins    │────────>│  Chrome Grid     │
│  Container   │         │  (Standalone)    │
│  Port: 8080  │         │  Port: 4444      │
└──────────────┘         │  VNC:  7900      │
                         └──────────────────┘
```

Jenkins runs tests → Grid provides browsers → Tests execute in isolated containers

---

## Next Steps

1. ✅ Configure webhook for auto-builds on commit
2. ✅ Add email notifications
3. ✅ Set up nightly test runs
4. ✅ Add more Grid nodes for parallel execution

---

For more details:
- [QUICK_REFERENCE.md](QUICK_REFERENCE.md) - Command cheat sheet
- [README.md](README.md) - Framework documentation

