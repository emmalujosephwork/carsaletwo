pipeline {
    agent any

    environment {
        // Docker Hub credentials ID (replace with your Jenkins credentials ID)
        DOCKER_HUB_CREDENTIALS = 'dockerhub-credentials-id' 
    }

    tools {
        // Maven tool configured in Jenkins (replace with your Maven tool name)
        maven 'Maven'
        // SonarQube Scanner tool name as configured in Jenkins
        sonarScanner 'SonarQubeScanner'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t emmalujoseph/carsaletwo:${BUILD_NUMBER} .'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    // Use the SonarQube Scanner installation and server name exactly as configured in Jenkins
                    def scannerHome = tool 'SonarQubeScanner'
                    withSonarQubeEnv('SonarQubeScanner') {
                        bat "\"${scannerHome}\\bin\\sonar-scanner.bat\" -Dsonar.projectKey=carsaletwo -Dsonar.sources=src -Dsonar.java.binaries=target/classes"
                    }
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
                withCredentials([usernamePassword(credentialsId: env.DOCKER_HUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                        echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                        docker push emmalujoseph/carsaletwo:${BUILD_NUMBER}
                        docker logout
                    """
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                echo 'Deploying to Dev environment'
                bat """
                    docker pull emmalujoseph/carsaletwo:${BUILD_NUMBER}
                    docker stop carsaletwo-dev || echo No running container to stop
                    docker rm carsaletwo-dev || echo No container to remove
                    docker run -d -p 8081:8080 --name carsaletwo-dev emmalujoseph/carsaletwo:${BUILD_NUMBER}
                """
            }
        }

        stage('Deploy to Test') {
            steps {
                echo 'Deploying to Test environment'
                bat """
                    docker pull emmalujoseph/carsaletwo:${BUILD_NUMBER}
                    docker stop carsaletwo-test || echo No running container to stop
                    docker rm carsaletwo-test || echo No container to remove
                    docker run -d -p 8082:8080 --name carsaletwo-test emmalujoseph/carsaletwo:${BUILD_NUMBER}
                """
            }
        }

        stage('Deploy to Prod') {
            steps {
                echo 'Deploying to Prod environment'
                bat """
                    docker pull emmalujoseph/carsaletwo:${BUILD_NUMBER}
                    docker stop carsaletwo-prod || echo No running container to stop
                    docker rm carsaletwo-prod || echo No container to remove
                    docker run -d -p 8080:8080 --name carsaletwo-prod emmalujoseph/carsaletwo:${BUILD_NUMBER}
                """
            }
        }
    }
}
