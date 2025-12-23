pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK25'
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
                    echo "Running ${params.TEST_SUITE} tests in ${params.BROWSER} browser (headless: ${params.HEADLESS})"

                    sh """
                        mvn test \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${params.HEADLESS} \
                        -DsuiteXmlFile=${suiteFile} \
                        -Dallure.results.directory=target/allure-results
                    """
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                script {
                    echo "Generating Allure report..."
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'target/allure-results']]
                    ])
                }
            }
        }
    }

    post {
        always {
            echo "Archiving test results and screenshots..."

            archiveArtifacts artifacts: 'target/screenshots/**/*.png', allowEmptyArchive: true

            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

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

