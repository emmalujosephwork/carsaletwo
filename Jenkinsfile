pipeline {
    agent any

    tools {
        maven 'Maven'  // Match your Maven installation name in Jenkins config
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

        stage('Deploy to Docker Desktop') {
            steps {
                // Stop and remove previous container if exists
                bat '''
                docker stop carsaletwo || echo Container not running
                docker rm carsaletwo || echo Container not found
                '''

                // Run the container
                bat '''
                docker run -d --name carsaletwo -p 8080:8080 %IMAGE_NAME%:%IMAGE_TAG%
                '''
            }
        }
    }
}
