pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "emmalujoseph/carsaletwo"
        DOCKER_TAG = "latest"
        DOCKER_CREDENTIALS = "dockerhub-creds"
        SONARQUBE_INSTALLATION = "SonarQubeScanner"
        MAVEN_TOOL = "Maven"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                tool name: "${MAVEN_TOOL}", type: 'maven'
                sh "${tool MAVEN_TOOL}/bin/mvn clean package"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_INSTALLATION}") {
                    sh "${tool MAVEN_TOOL}/bin/mvn sonar:sonar -Dsonar.projectKey=carsaletwo -Dsonar.sources=src/main/java -Dsonar.java.binaries=target/classes"
                }
            }
        }

        stage('Test') {
            steps {
                sh "${tool MAVEN_TOOL}/bin/mvn test"
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    dockerImage = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                    sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker logout"
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                script {
                    sh "docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker stop carsaletwo-dev || true"
                    sh "docker rm carsaletwo-dev || true"
                    sh "docker run -d -p 8081:8080 --name carsaletwo-dev ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }

        stage('Deploy to Test') {
            steps {
                script {
                    sh "docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker stop carsaletwo-test || true"
                    sh "docker rm carsaletwo-test || true"
                    sh "docker run -d -p 8082:8080 --name carsaletwo-test ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }

        stage('Deploy to Prod') {
            steps {
                script {
                    sh "docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker stop carsaletwo-prod || true"
                    sh "docker rm carsaletwo-prod || true"
                    sh "docker run -d -p 8080:8080 --name carsaletwo-prod ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
