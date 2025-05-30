pipeline {
    agent any

    tools {
        maven 'Maven'  // Make sure 'Maven' matches your Maven installation name in Jenkins
    }

    environment {
        IMAGE_NAME = 'yourdockerhubusername/carsaletwo'
        IMAGE_TAG = "${env.BUILD_NUMBER}"  // Removed redundant 'carsaletwo:' prefix from tag
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
