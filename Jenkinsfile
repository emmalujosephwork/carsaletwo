pipeline {
    agent any

    tools {
        maven 'Maven' // your Maven installation name in Jenkins
    }

    environment {
        IMAGE_NAME = 'emmalujoseph/carsaletwo'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
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
            steps {
                withSonarQubeEnv('SonarQubeScanner') {
                    bat """
                        sonar-scanner ^
                        -Dsonar.projectKey=carsaletwo ^
                        -Dsonar.sources=src ^
                        -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Push Docker Image') {
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
                echo 'Deploying to Development environment'
                bat """
                    docker stop carsaletwo-dev || echo Container not running
                    docker rm carsaletwo-dev || echo Container not found
                    docker run -d --name carsaletwo-dev -p 8081:8080 %IMAGE_NAME%:%IMAGE_TAG%
                """
            }
        }

        stage('Deploy to Test') {
            steps {
                echo 'Deploying to Test environment'
                bat """
                    docker stop carsaletwo-test || echo Container not running
                    docker rm carsaletwo-test || echo Container not found
                    docker run -d --name carsaletwo-test -p 8082:8080 %IMAGE_NAME%:%IMAGE_TAG%
                """
            }
        }

        stage('Deploy to Prod') {
            steps {
                echo 'Deploying to Production environment'
                bat """
                    docker stop carsaletwo-prod || echo Container not running
                    docker rm carsaletwo-prod || echo Container not found
                    docker run -d --name carsaletwo-prod -p 8080:8080 %IMAGE_NAME%:%IMAGE_TAG%
                """
            }
        }
    }
}
