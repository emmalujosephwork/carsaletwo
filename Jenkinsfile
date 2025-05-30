pipeline {
    agent any

    tools {
        maven 'Maven'  // Your Maven tool name in Jenkins config
    }

    environment {
        IMAGE_NAME = 'emmalujoseph/carsaletwo'  // Use your Docker Hub repo name
        IMAGE_TAG = "carsaletwo:${env.BUILD_NUMBER}"
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
        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }
        stage('Deploy to Test') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat 'docker login -u %DOCKER_USER% -p %DOCKER_PASS%'
                    bat "docker push %IMAGE_NAME%:%IMAGE_TAG%"
                }
            }
        }
    }
}
