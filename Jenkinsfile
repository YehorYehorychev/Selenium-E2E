pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'safari'],
            description: 'Browser to run tests on'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode'
        )
    }

    environment {
        MAVEN_OPTS = '-Xmx2048m -XX:MaxMetaspaceSize=512m'
        SELENIUM_GRID_URL = getSeleniumGridUrl("${params.BROWSER}")
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code..."
                checkout scm
            }
        }

        stage('Clean') {
            steps {
                echo "Cleaning previous build artifacts..."
                sh 'mvn clean'
            }
        }

        stage('Compile') {
            steps {
                echo "Compiling project..."
                sh 'mvn compile test-compile'

                echo "Verifying Allure installation..."
                sh 'allure --version || echo "WARNING: Allure not found in PATH"'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo "=========================================="
                    echo "Cucumber BDD Test Execution:"
                    echo "  Browser: ${params.BROWSER}"
                    echo "  Headless: ${params.HEADLESS}"
                    echo "  Grid URL: ${env.SELENIUM_GRID_URL}"
                    echo "  Suite: testng-cucumber.xml"
                    echo "=========================================="

                    // Verify suite file exists
                    sh "test -f src/test/resources/testng-cucumber.xml && echo 'Cucumber suite found' || echo 'WARNING: Suite file not found!'"

                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                        sh """
                            mvn clean test \
                            -Dbrowser=${params.BROWSER} \
                            -Dheadless=${params.HEADLESS} \
                            -Dselenium.grid.url=${env.SELENIUM_GRID_URL} \
                            -DsuiteXmlFile=src/test/resources/testng-cucumber.xml \
                            -Dallure.results.directory=target/allure-results
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Archiving test results and screenshots..."

            // Archive screenshots from actual location
            archiveArtifacts artifacts: 'src/test/resources/assets/screenshots/**/*.png', allowEmptyArchive: true

            // Archive surefire reports
            archiveArtifacts artifacts: 'target/surefire-reports/**/*.xml', allowEmptyArchive: true

            // Publish JUnit test results
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

            // Generate Allure Report
            script {
                echo "Generating Allure report..."

                sh 'ls -la target/allure-results/ || echo "No allure-results found"'
                sh 'find target/allure-results -type f | head -10 || echo "No files in allure-results"'

                sh '''
                    if [ -d "target/allure-results" ] && [ "$(ls -A target/allure-results)" ]; then
                        echo "Generating Allure HTML report..."
                        allure generate target/allure-results -o target/allure-report --clean
                        echo "✅ Allure report generated at target/allure-report/index.html"
                    else
                        echo "⚠️ No Allure results found, skipping report generation"
                    fi
                '''
            }

            // Archive Allure HTML report
            archiveArtifacts artifacts: 'target/allure-report/**/*', allowEmptyArchive: true

            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/allure-report',
                reportFiles: 'index.html',
                reportName: 'Allure Report',
                reportTitles: 'Allure Test Report'
            ])

            script {
                try {
                    allure([
                        includeProperties: false,
                        jdk: '',
                        results: [[path: 'target/allure-results']],
                        reportBuildPolicy: 'ALWAYS'
                    ])
                } catch (Exception e) {
                    echo "⚠️ Allure plugin not configured, using HTML Publisher instead"
                    echo "View report: Click 'Allure Report' link in build sidebar"
                }
            }

            // cleanWs()
        }

        success {
            echo "✅ Build completed successfully!"
        }

        failure {
            echo "❌ Build failed!"
        }

        unstable {
            echo "⚠️ Build is unstable - some tests failed"
        }
    }
}


def getSeleniumGridUrl(browser) {
    def gridHost = env.SELENIUM_GRID_HOST ?: 'selenium-chrome'
    switch(browser?.toLowerCase()) {
        case 'chrome':
            return "http://selenium-chrome:4444/wd/hub"
        case 'firefox':
            return "http://selenium-firefox:4444/wd/hub"
        default:
            return "http://selenium-chrome:4444/wd/hub"
    }
}
