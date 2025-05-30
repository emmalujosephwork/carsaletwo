pipeline {
    agent any

    tools {
        maven 'Maven'            // your Maven installation name
        sonarQubeScanner 'SonarQubeScanner'  // your SonarQube Scanner installation name
    }

    environment {
        IMAGE_NAME = 'emmalujoseph/carsaletwo'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        SONARQUBE_ENV = 'SonarQubeScanner'  // Must match the scanner name above
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
                script {
                    echo 'Deploying to Development Environment'
                    bat """
                    docker stop carsaletwo-dev || echo Container not running
                    docker rm carsaletwo-dev || echo Container not found
                    docker run -d -p 8081:8080 --name carsaletwo-dev %IMAGE_NAME%:%IMAGE_TAG%
                    """
                }
            }
        }

        stage('Deploy to Test') {
            steps {
                script {
                    echo 'Deploying to Test Environment'
                    bat """
                    docker stop carsaletwo-test || echo Container not running
                    docker rm carsaletwo-test || echo Container not found
                    docker run -d -p 8082:8080 --name carsaletwo-test %IMAGE_NAME%:%IMAGE_TAG%
                    """
                }
            }
        }

        stage('Deploy to Prod') {
            steps {
                script {
                    echo 'Deploying to Production Environment'
                    bat """
                    docker stop carsaletwo-prod || echo Container not running
                    docker rm carsaletwo-prod || echo Container not found
                    docker run -d -p 8080:8080 --name carsaletwo-prod %IMAGE_NAME%:%IMAGE_TAG%
                    """
                }
            }
        }
    }
}
