pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "emmalujoseph/carsaletwo"
        DOCKER_TAG = "latest"
        DOCKER_CREDENTIALS = "dockerhub-creds"    // your DockerHub credentials ID
        SONARQUBE_INSTALLATION = "SonarQubeScanner"  // your SonarQube Scanner installation name in Jenkins
        MAVEN_TOOL = "Maven"   // your Maven tool installation name in Jenkins
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
                bat "\"${tool MAVEN_TOOL}\\bin\\mvn.cmd\" clean package"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_INSTALLATION}") {
                    bat "\"${tool MAVEN_TOOL}\\bin\\mvn.cmd\" sonar:sonar -Dsonar.projectKey=carsaletwo -Dsonar.sources=src/main/java -Dsonar.java.binaries=target/classes"
                }
            }
        }

        stage('Test') {
            steps {
                bat "\"${tool MAVEN_TOOL}\\bin\\mvn.cmd\" test"
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
                    bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                    bat "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    bat "docker logout"
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                bat """
                docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                docker stop carsaletwo-dev || exit 0
                docker rm carsaletwo-dev || exit 0
                docker run -d -p 8081:8080 --name carsaletwo-dev ${DOCKER_IMAGE}:${DOCKER_TAG}
                """
            }
        }

        stage('Deploy to Test') {
            steps {
                bat """
                docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                docker stop carsaletwo-test || exit 0
                docker rm carsaletwo-test || exit 0
                docker run -d -p 8082:8080 --name carsaletwo-test ${DOCKER_IMAGE}:${DOCKER_TAG}
                """
            }
        }

        stage('Deploy to Prod') {
            steps {
                bat """
                docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                docker stop carsaletwo-prod || exit 0
                docker rm carsaletwo-prod || exit 0
                docker run -d -p 8080:8080 --name carsaletwo-prod ${DOCKER_IMAGE}:${DOCKER_TAG}
                """
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
