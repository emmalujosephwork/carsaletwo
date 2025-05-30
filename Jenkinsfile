pipeline {
    agent any

    tools {
        maven 'Maven'  // Match your Maven tool name in Jenkins config
    }

    environment {
        IMAGE_NAME = 'yourdockerhubusername/carsaletwo'
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
                bat "docker push %IMAGE_NAME%:%IMAGE_TAG%"
            }
        }
    }
}
