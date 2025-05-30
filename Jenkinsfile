pipeline {
    agent any

    environment {
        IMAGE_NAME = 'yourdockerhubusername/carsaletwo'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
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
                // Add your deploy commands here if needed
            }
        }
    }
}
