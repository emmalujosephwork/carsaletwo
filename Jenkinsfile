pipeline {
    agent any

    environment {
        IMAGE_NAME = 'yourdockerhubusername/carsaletwo'
        IMAGE_TAG = "carsaletwo:${env.BUILD_NUMBER}"
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
                sh "docker build -t $IMAGE_NAME:$IMAGE_TAG ."
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Deploy to Test') {
            steps {
                sh "docker push $IMAGE_NAME:$IMAGE_TAG"
                // Add your deploy commands here (e.g., ssh to server, kubectl apply, etc.)
            }
        }
    }
}
