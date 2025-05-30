pipeline {
    agent any

    environment {
        DOCKERHUB_USER = 'emmalujoseph'
        DOCKER_IMAGE = "${DOCKERHUB_USER}/carsaletwo"
        DOCKER_TAG = "latest"  // Change tag as needed
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
                    def mvnHome = tool 'Maven'  // Your Jenkins Maven installation name
                    bat "\"${mvnHome}\\bin\\mvn.cmd\" clean package"
                }
            }
        }

        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarQubeScanner'  // Adjust if your tool name differs
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
                    bat """
                    docker login -u %DOCKERHUB_USER_VAR% -p %DOCKERHUB_PASS%
                    docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                    docker logout
                    """
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                echo "Deploying to Development environment"
                bat """
                ssh devuser@dev-server "docker pull ${DOCKER_IMAGE}:${DOCKER_TAG} && docker stop carsaletwo || true && docker rm carsaletwo || true && docker run -d --name carsaletwo -p 8080:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}"
                """
            }
        }

        stage('Deploy to Test') {
            steps {
                echo "Deploying to Test environment"
                bat """
                ssh testuser@test-server "docker pull ${DOCKER_IMAGE}:${DOCKER_TAG} && docker stop carsaletwo || true && docker rm carsaletwo || true && docker run -d --name carsaletwo -p 8080:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}"
                """
            }
        }

        stage('Deploy to Prod') {
            steps {
                echo "Deploying to Production environment"
                bat """
                ssh produser@prod-server "docker pull ${DOCKER_IMAGE}:${DOCKER_TAG} && docker stop carsaletwo || true && docker rm carsaletwo || true && docker run -d --name carsaletwo -p 8080:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}"
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
        }
        failure {
            echo "Pipeline failed. Please check the logs."
        }
    }
}
