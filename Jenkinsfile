pipeline {
    agent any

    environment {
        DOCKERHUB_USER = 'emmalujoseph'  // Your Docker Hub username
        DOCKER_IMAGE = "${DOCKERHUB_USER}/carsaletwo"
        DOCKER_TAG = "latest"
        
        // Replace these with your real server IPs or hostnames
        DEV_SERVER = "192.168.1.100"
        TEST_SERVER = "192.168.1.101"
        PROD_SERVER = "192.168.1.102"
        
        DEV_USER = "devuser"   // SSH user for dev server
        TEST_USER = "testuser" // SSH user for test server
        PROD_USER = "produser" // SSH user for prod server
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
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', 
                                                 usernameVariable: 'DOCKERHUB_USER_VAR', 
                                                 passwordVariable: 'DOCKERHUB_PASS')]) {
                    bat '''
                    docker login -u %DOCKERHUB_USER_VAR% -p %DOCKERHUB_PASS%
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                bat """
                docker build -t %DOCKER_IMAGE%:%DOCKER_TAG% .
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', 
                                                 usernameVariable: 'DOCKERHUB_USER_VAR', 
                                                 passwordVariable: 'DOCKERHUB_PASS')]) {
                    bat """
                    docker login -u %DOCKERHUB_USER_VAR% -p %DOCKERHUB_PASS%
                    docker push %DOCKER_IMAGE%:%DOCKER_TAG%
                    docker logout
                    """
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                echo "Deploying to Dev Server"
                bat """
                ssh %DEV_USER%@%DEV_SERVER% "docker pull %DOCKER_IMAGE%:%DOCKER_TAG% && docker stop carsaletwo || true && docker rm carsaletwo || true && docker run -d --name carsaletwo -p 8080:8080 %DOCKER_IMAGE%:%DOCKER_TAG%"
                """
            }
        }

        stage('Deploy to Test') {
            steps {
                echo "Deploying to Test Server"
                bat """
                ssh %TEST_USER%@%TEST_SERVER% "docker pull %DOCKER_IMAGE%:%DOCKER_TAG% && docker stop carsaletwo || true && docker rm carsaletwo || true && docker run -d --name carsaletwo -p 8080:8080 %DOCKER_IMAGE%:%DOCKER_TAG%"
                """
            }
        }

        stage('Deploy to Prod') {
            steps {
                echo "Deploying to Prod Server"
                bat """
                ssh %PROD_USER%@%PROD_SERVER% "docker pull %DOCKER_IMAGE%:%DOCKER_TAG% && docker stop carsaletwo || true && docker rm carsaletwo || true && docker run -d --name carsaletwo -p 8080:8080 %DOCKER_IMAGE%:%DOCKER_TAG%"
                """
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        failure {
            echo "Pipeline failed. Please check the logs."
        }
    }
}
