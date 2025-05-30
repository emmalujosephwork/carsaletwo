pipeline {
    agent any

    tools {
        maven 'Maven'     // Your Maven tool name in Jenkins (check Manage Jenkins > Global Tool Configuration)
        jdk 'jdk17'       // Your JDK 17 installation name in Jenkins
    }

    environment {
        SONARQUBE_SCANNER = 'SonarQubeScanner'  // SonarQube server installation name configured in Jenkins
        DOCKER_IMAGE = "emmalujoseph/carsaletwo:latest"
        REGISTRY_CREDENTIALS = 'dockerhub-credentials'  // Your DockerHub credential ID in Jenkins
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat '"${MAVEN_HOME}\\bin\\mvn.cmd" clean package'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(SONARQUBE_SCANNER) {
                    bat '"${MAVEN_HOME}\\bin\\mvn.cmd" sonar:sonar -Dsonar.projectKey=carsaletwo -Dsonar.sources=src/main/java -Dsonar.java.binaries=target/classes'
                }
            }
        }

        stage('Test') {
            steps {
                bat '"${MAVEN_HOME}\\bin\\mvn.cmd" test'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: REGISTRY_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                    bat "docker push ${DOCKER_IMAGE}"
                    bat "docker logout"
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                echo "Deploying to Dev environment..."
                bat """
                docker pull ${DOCKER_IMAGE}
                docker stop carsaletwo-dev || echo No running dev container to stop
                docker rm carsaletwo-dev || echo No dev container to remove
                docker run -d -p 8081:8080 --name carsaletwo-dev ${DOCKER_IMAGE}
                """
            }
        }

        stage('Deploy to Test') {
            steps {
                echo "Deploying to Test environment..."
                bat """
                docker pull ${DOCKER_IMAGE}
                docker stop carsaletwo-test || echo No running test container to stop
                docker rm carsaletwo-test || echo No test container to remove
                docker run -d -p 8082:8080 --name carsaletwo-test ${DOCKER_IMAGE}
                """
            }
        }

        stage('Deploy to Prod') {
            steps {
                input message: 'Approve deployment to Production?', ok: 'Deploy'
                echo "Deploying to Prod environment..."
                bat """
                docker pull ${DOCKER_IMAGE}
                docker stop carsaletwo-prod || echo No running prod container to stop
                docker rm carsaletwo-prod || echo No prod container to remove
                docker run -d -p 8080:8080 --name carsaletwo-prod ${DOCKER_IMAGE}
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
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}
