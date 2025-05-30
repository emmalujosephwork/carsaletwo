pipeline {
    agent any
    tools {
        maven 'Maven'
    }
    environment {
        IMAGE_NAME = 'emmalujoseph/carsaletwo'
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
                bat "docker build -t %IMAGE_NAME%:%BUILD_NUMBER% ."
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
                    usernameVariable: 'DOCKERHUB_USER',
                    passwordVariable: 'DOCKERHUB_PASS')]) {
                    bat '''
                        echo %DOCKERHUB_PASS% | docker login -u %DOCKERHUB_USER% --password-stdin
                        docker push %IMAGE_NAME%:%BUILD_NUMBER%
                        docker logout
                    '''
                }
            }
        }
        stage('Deploy to Dev') {
            steps {
                bat "docker run -d --rm --name carsaletwo-dev -p 8081:8080 %IMAGE_NAME%:%BUILD_NUMBER%"
            }
        }
        stage('Deploy to Test') {
            steps {
                bat "docker run -d --rm --name carsaletwo-test -p 8082:8080 %IMAGE_NAME%:%BUILD_NUMBER%"
            }
        }
        stage('Deploy to Prod') {
            steps {
                // Add production deployment script here (can be manual approval step)
                echo 'Deploying to production environment'
                // Example: bat "docker run -d --rm --name carsaletwo-prod -p 8080:8080 %IMAGE_NAME%:%BUILD_NUMBER%"
            }
        }
    }
}
