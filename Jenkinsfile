pipeline {
    agent any

    tools {
        maven 'Maven'       // Replace with your Maven tool name in Jenkins
        jdk 'jdk11'         // Replace with your JDK tool name in Jenkins
    }

    environment {
        SONARQUBE_SCANNER = 'SonarQubeScanner'  // Your SonarQube server installation name
        DOCKER_IMAGE = "emmalujoseph/carsaletwo:38"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat '"${MAVEN_HOME}\\bin\\mvn.cmd" clean package'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(SONARQUBE_SCANNER) {
                    bat '"${MAVEN_HOME}\\bin\\mvn.cmd" sonar:sonar -Dsonar.projectKey=carsaletwo -Dsonar.sources=src/main/java -Dsonar.java.binaries=target/classes'
                }
            }
        }

        stage('Test') {
            steps {
                bat '"${MAVEN_HOME}\\bin\\mvn.cmd" test'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                bat "docker push ${DOCKER_IMAGE}"
            }
        }

        stage('Deploy to Dev') {
            steps {
                echo 'Deploying to Development environment...'
                // Example deployment command, replace with your real deploy script or commands:
                // bat 'kubectl apply -f k8s/dev-deployment.yaml'
            }
        }

        stage('Deploy to Test') {
            steps {
                echo 'Deploying to Test environment...'
                // Replace with your actual deployment commands:
                // bat 'kubectl apply -f k8s/test-deployment.yaml'
            }
        }

        stage('Deploy to Prod') {
            steps {
                input message: 'Approve deployment to Production?', ok: 'Deploy'
                echo 'Deploying to Production environment...'
                // Replace with your actual deployment commands:
                // bat 'kubectl apply -f k8s/prod-deployment.yaml'
            }
        }
    }

    post {
        always {
            echo 'Cleaning workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
