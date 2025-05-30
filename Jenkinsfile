pipeline {
    agent any

    environment {
        DOCKERHUB_USER = 'emmalujoseph'
        DOCKER_IMAGE = "${DOCKERHUB_USER}/carsaletwo"
        DOCKER_TAG = "latest" // Change tag as needed
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    def mvnHome = tool 'Maven' // Your Jenkins Maven installation name
                    bat "\"${mvnHome}\\bin\\mvn.cmd\" clean package"
                }
            }
        }

        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarQubeScanner' // Adjust if your tool name differs
            }
            steps {
                withSonarQubeEnv('SonarQubeScanner') {
                    bat "\"${scannerHome}\\bin\\sonar-scanner.bat\" -Dsonar.projectKey=carsaletwo -Dsonar.sources=src/main/java -Dsonar.java.binaries=target/classes"
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    def mvnHome = tool 'Maven'
                    bat "\"${mvnHome}\\bin\\mvn.cmd\" test"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
            }
        }

        stage('Docker Login & Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                 usernameVariable: 'DOCKERHUB_USER_VAR',
                                                 passwordVariable: 'DOCKERHUB_PASS')]) {
                    bat '''
                        docker login -u %DOCKERHUB_USER_VAR% -p %DOCKERHUB_PASS%
                        docker push %DOCKER_IMAGE%:%DOCKER_TAG%
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                echo "Deploying to Development environment (Local)"
                bat """
                    docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                    docker stop carsaletwo-dev 2>nul || echo Container not running
                    docker rm carsaletwo-dev 2>nul || echo Container does not exist
                    docker run -d --name carsaletwo-dev -p 8081:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}
                    echo Development deployment completed on port 8081
                """
            }
        }

        stage('Deploy to Test') {
            steps {
                echo "Deploying to Test environment (Local)"
                bat """
                    docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                    docker stop carsaletwo-test 2>nul || echo Container not running
                    docker rm carsaletwo-test 2>nul || echo Container does not exist
                    docker run -d --name carsaletwo-test -p 8082:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}
                    echo Test deployment completed on port 8082
                """
            }
        }

        stage('Deploy to Prod') {
            steps {
                echo "Deploying to Production environment (Local)"
                bat """
                    docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                    docker stop carsaletwo-prod 2>nul || echo Container not running
                    docker rm carsaletwo-prod 2>nul || echo Container does not exist
                    docker run -d --name carsaletwo-prod -p 8083:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}
                    echo Production deployment completed on port 8083
                """
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully."
            echo "Applications deployed:"
            echo "- Dev: http://localhost:8081"
            echo "- Test: http://localhost:8082" 
            echo "- Prod: http://localhost:8083"
        }
        failure {
            echo "Pipeline failed. Please check the logs."
        }
    }
}