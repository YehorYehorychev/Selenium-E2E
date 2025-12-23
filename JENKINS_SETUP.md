# Jenkins Setup Guide for Test Execution

## 📋 Prerequisites

### 1. Jenkins Installation
```bash
# macOS (via Homebrew)
brew install jenkins-lts
brew services start jenkins-lts

# Or download from official website
# https://www.jenkins.io/download/
```

After installation, Jenkins will be available at: `http://localhost:8080`

### 2. Initial Jenkins Setup

1. **Unlock Jenkins**
   - Open `http://localhost:8080`
   - Find the administrator password in the file:
     ```bash
     cat ~/.jenkins/secrets/initialAdminPassword
     ```
   - Paste the password into the web interface

2. **Install Plugins**
   - Choose "Install suggested plugins"
   - Additionally install:
     - **Allure Plugin** (for reports)
     - **Pipeline Plugin** (usually already installed)
     - **Git Plugin** (for Git integration)
     - **Maven Integration Plugin**
     - **TestNG Results Plugin**

---

## ⚙️ Global Tool Configuration

### 1. JDK Setup
1. Navigate to: `Dashboard → Manage Jenkins → Global Tool Configuration`
2. Find the **JDK** section
3. Click "Add JDK"
4. Fill in:
   - **Name**: `JDK17` (must match Jenkinsfile)
   - **JAVA_HOME**: path to your JDK
     ```bash
     # For macOS, find the path with:
     /usr/libexec/java_home -v 17
     # Example: /Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
     ```
   - Uncheck "Install automatically" if using existing JDK

### 2. Maven Setup
1. On the same page, find the **Maven** section
2. Click "Add Maven"
3. Fill in:
   - **Name**: `Maven3` (must match Jenkinsfile)
   - **MAVEN_HOME**: path to Maven (if installed locally)
     ```bash
     # Find the path with:
     mvn -version
     # Or use automatic installation
     ```
   - Or select "Install automatically" and choose version (e.g., 3.9.6)

### 3. Allure Setup
1. On the same page, find the **Allure Commandline** section
2. Click "Add Allure Commandline"
3. Fill in:
   - **Name**: `Allure`
   - Check "Install automatically"
   - Select the latest version (e.g., 2.25.0)

4. Click **Save**

---

## 🚀 Creating Jenkins Job (Pipeline)

### Option 1: Pipeline from Repository (Recommended)

1. **Create New Job**
   - Dashboard → New Item
   - Enter name: `Selenium-Framework-Tests`
   - Select type: **Pipeline**
   - Click OK

2. **Configure Pipeline**
   - In the **Pipeline** section:
     - **Definition**: Pipeline script from SCM
     - **SCM**: Git
     - **Repository URL**: Your Git repository URL
       ```
       https://github.com/yourusername/selenium-framework.git
       ```
     - **Credentials**: add if repository is private
     - **Branch**: `*/main` (or your branch)
     - **Script Path**: `Jenkinsfile`

3. **Build Parameters** (optional)
   - Already configured in Jenkinsfile via `parameters` block
   - Jenkins will automatically detect them after the first run

4. **Save**

### Option 2: Pipeline Script Directly

1. Create a new Pipeline Job (as above)
2. In the **Pipeline** section:
   - **Definition**: Pipeline script
   - Copy the contents of `Jenkinsfile` into the text field
3. Save

---

## 🔧 Browser Setup on Jenkins Agent

### For Headless Mode (Recommended for CI)

#### macOS/Linux:
```bash
# Install Chrome
brew install --cask google-chrome

# Install Firefox
brew install --cask firefox

# Install ChromeDriver (if needed)
brew install chromedriver

# Install GeckoDriver (if needed)
brew install geckodriver
```

#### Linux (Ubuntu/Debian):
```bash
# Chrome
wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
sudo apt install ./google-chrome-stable_current_amd64.deb

# Firefox
sudo apt install firefox

# ChromeDriver (webdrivermanager usually does this automatically)
```

### For Docker Agent

You can create a `Dockerfile`:
```dockerfile
FROM maven:3.9-eclipse-temurin-17

# Install Chrome
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Firefox
RUN apt-get update && apt-get install -y firefox-esr

WORKDIR /app
```

---

## ▶️ Running Tests

### Via Web Interface:
1. Open your Job: `Selenium-Framework-Tests`
2. Click **Build with Parameters**
3. Select parameters:
   - **BROWSER**: chrome / firefox / edge
   - **TEST_SUITE**: all / shopping / greenkart / amazon / flightbooking / practice
   - **HEADLESS**: true / false
4. Click **Build**

### Via Jenkins CLI:
```bash
# Download jenkins-cli.jar
wget http://localhost:8080/jnlpJars/jenkins-cli.jar

# Run build
java -jar jenkins-cli.jar -s http://localhost:8080/ build Selenium-Framework-Tests \
  -p BROWSER=chrome \
  -p TEST_SUITE=shopping \
  -p HEADLESS=true
```

---

## 📊 Viewing Results

### 1. TestNG Results
- After the build, a **Test Result** section will appear on the Job page
- Shows the number of passed/failed/skipped tests

### 2. Allure Report
- On the build page, click **Allure Report**
- Opens a detailed report with:
  - Graphs
  - Execution history
  - Test steps (@Step)
  - Screenshots (attachments)

### 3. Screenshots
- Available in **Build Artifacts**
- Path: `target/screenshots/`

### 4. Console Output
- Click on the build number → **Console Output**
- View execution logs

---

## 🔄 Automated Triggers

### Poll SCM (Check for Git Changes)
1. In Job settings, find **Build Triggers**
2. Check **Poll SCM**
3. In the Schedule field, enter a cron expression:
   ```
   # Check every 5 minutes
   H/5 * * * *
   
   # Or every hour
   H * * * *
   ```

### Webhook (On Git Push)
1. In GitHub/GitLab, configure webhook:
   - URL: `http://your-jenkins-url:8080/github-webhook/`
2. In Jenkins, check **GitHub hook trigger for GITScm polling**

### Scheduled Builds
1. Check **Build periodically**
2. Enter a cron expression:
   ```
   # Daily at 02:00
   0 2 * * *
   
   # Every Monday at 09:00
   0 9 * * 1
   
   # Every weekday at 18:00
   0 18 * * 1-5
   ```

---

## 🐛 Troubleshooting

### Issue: "No such tool Maven3"
**Solution**: Verify that in Global Tool Configuration the Maven name is exactly `Maven3`

### Issue: "Chrome not found"
**Solution**: 
```bash
# Install Chrome and ensure headless=true
brew install --cask google-chrome
```

### Issue: "Permission denied" for screenshots
**Solution**:
```bash
# Grant permissions to workspace folder
chmod -R 755 ~/.jenkins/workspace/
```

### Issue: Tests fail due to timeout
**Solution**: Increase timeouts in `config.properties`:
```properties
wait.default.seconds=20
wait.poll.millis=500
```

### Issue: Allure report not generated
**Solution**:
1. Ensure Allure Plugin is installed
2. Verify that pom.xml has allure dependencies
3. Check that `target/allure-results` exists after tests

---

## 📧 Notifications (Optional)

### Email Notifications
1. Install **Email Extension Plugin**
2. `Manage Jenkins → Configure System → Extended E-mail Notification`
3. Configure SMTP server
4. Add to Jenkinsfile:
```groovy
post {
    failure {
        emailext (
            subject: "❌ Test Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
            body: "Check console output at ${env.BUILD_URL}",
            to: "your-email@example.com"
        )
    }
}
```

### Slack Notifications
1. Install **Slack Notification Plugin**
2. Configure integration with Slack workspace
3. Add to Jenkinsfile:
```groovy
post {
    always {
        slackSend (
            color: currentBuild.result == 'SUCCESS' ? 'good' : 'danger',
            message: "Build ${currentBuild.result}: ${env.JOB_NAME} - ${env.BUILD_NUMBER}"
        )
    }
}
```

---

## 🎯 Best Practices

1. **Use headless mode** in CI for stability
2. **Run tests in parallel** for speed (already configured in testng.xml)
3. **Store secrets** in Jenkins Credentials, not in code
4. **Archive artifacts** (screenshots, reports)
5. **Configure notifications** for failures
6. **Use separate agents** for different environments
7. **Version control Jenkinsfile** in Git with the project

---

## 📚 Additional Resources

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Allure Jenkins Plugin](https://docs.qameta.io/allure/#_jenkins)
- [TestNG Jenkins Plugin](https://plugins.jenkins.io/testng-plugin/)


