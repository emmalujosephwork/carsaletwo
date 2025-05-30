pipeline {
    agent any

    tools {
        maven 'Maven'                // Your Maven installation name in Jenkins
        sonarRunner 'SonarQubeScanner'  // Your SonarQube Scanner installation name in Jenkins
    }

    environment {
        IMAGE_NAME = 'emmalujoseph/carsaletwo'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        SONARQUBE_ENV = 'SonarQubeScanner'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package'
                bat "docker build -t %IMAGE_NAME%:%IMAGE_TAG% ."
            }
        }

        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarQubeScanner'
            }
            steps {
                withSonarQubeEnv('SonarQubeScanner') {
                    bat "${scannerHome}/bin/sonar-scanner.bat -Dsonar.projectKey=carsaletwo -Dsonar.sources=src"
                }
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Push Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', 
                                                 usernameVariable: 'DOCKERHUB_USER_VAR', 
                                                 passwordVariable: 'DOCKERHUB_PASS')]) {
                    bat '''
                        echo %DOCKERHUB_PASS% | docker login -u %DOCKERHUB_USER_VAR% --password-stdin
                        docker push %IMAGE_NAME%:%IMAGE_TAG%
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                echo "Deploying to Development Environment"
                // Example: deploy with dev-specific Docker image tag or env variables
                bat "docker pull %IMAGE_NAME%:%IMAGE_TAG%"
                bat "docker run -d -p 8081:8080 --name carsaletwo-dev %IMAGE_NAME%:%IMAGE_TAG%"
            }
        }

        stage('Deploy to Test') {
            steps {
                echo "Deploying to Test Environment"
                bat "docker pull %IMAGE_NAME%:%IMAGE_TAG%"
                bat "docker stop carsaletwo-test || echo Not running"
                bat "docker rm carsaletwo-test || echo Not found"
                bat "docker run -d -p 8082:8080 --name carsaletwo-test %IMAGE_NAME%:%IMAGE_TAG%"
            }
        }

        stage('Deploy to Prod') {
            steps {
                echo "Deploying to Production Environment"
                bat "docker pull %IMAGE_NAME%:%IMAGE_TAG%"
                bat "docker stop carsaletwo-prod || echo Not running"
                bat "docker rm carsaletwo-prod || echo Not found"
                bat "docker run -d -p 8080:8080 --name carsaletwo-prod %IMAGE_NAME%:%IMAGE_TAG%"
            }
        }
    }
}
