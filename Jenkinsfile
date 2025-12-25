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
        choice(
            name: 'TEST_SUITE',
            choices: ['all', 'shopping', 'greenkart', 'amazon', 'flightbooking', 'practice'],
            description: 'Test suite to execute'
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
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    def suiteFile = getSuiteFile(params.TEST_SUITE)
                    echo "=========================================="
                    echo "Test Execution Configuration:"
                    echo "  Suite: ${params.TEST_SUITE}"
                    echo "  Browser: ${params.BROWSER}"
                    echo "  Headless: ${params.HEADLESS}"
                    echo "  Grid URL: ${env.SELENIUM_GRID_URL}"
                    echo "  Suite File: ${suiteFile}"
                    echo "=========================================="

                    // Verify suite file exists
                    sh "test -f ${suiteFile} && echo 'Suite file found' || echo 'WARNING: Suite file not found!'"

                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                        sh """
                            mvn clean test \
                            -Dbrowser=${params.BROWSER} \
                            -Dheadless=${params.HEADLESS} \
                            -Dselenium.grid.url=${env.SELENIUM_GRID_URL} \
                            -DsuiteXmlFile=${suiteFile} \
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
                allure([
                    includeProperties: false,
                    jdk: '',
                    commandline: 'Allure',
                    results: [[path: 'target/allure-results']],
                    reportBuildPolicy: 'ALWAYS'
                ])
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

def getSuiteFile(suite) {
    switch(suite) {
        case 'all':
            return 'src/test/resources/testng.xml'
        case 'shopping':
            return 'src/test/resources/testng-shopping.xml'
        case 'greenkart':
            return 'src/test/resources/testng-greenkart.xml'
        case 'amazon':
            return 'src/test/resources/testng-amazon.xml'
        case 'flightbooking':
            return 'src/test/resources/testng-flightbooking.xml'
        case 'practice':
            return 'src/test/resources/testng-practice.xml'
        default:
            return 'src/test/resources/testng.xml'
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
