pipeline {
    agent any

    tools {
        maven 'Maven'  // Your Maven tool name in Jenkins
        // SonarQube scanner is called inside the stage with tool()
    }

    environment {
        IMAGE_NAME = 'emmalujoseph/carsaletwo'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        DOCKERHUB_CREDS = 'dockerhub-creds'   // Your DockerHub credentials ID in Jenkins
        SONAR_SCANNER = 'SonarQubeScanner'    // Your SonarQube scanner tool name in Jenkins
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
                bat "docker build -t %IMAGE_NAME%:%IMAGE_TAG% ."
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool name: env.SONAR_SCANNER, type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                    withSonarQubeEnv(env.SONAR_SCANNER) {
                        bat """
                        "${scannerHome}\\bin\\sonar-scanner.bat" ^
                        -Dsonar.projectKey=carsaletwo ^
                        -Dsonar.sources=src ^
                        -Dsonar.java.binaries=target/classes
                        """
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
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDS, 
                                                 usernameVariable: 'DOCKERHUB_USER_VAR', 
                                                 passwordVariable: 'DOCKERHUB_PASS')]) {
                    bat """
                    echo %DOCKERHUB_PASS% | docker login -u %DOCKERHUB_USER_VAR% --password-stdin
                    docker push %IMAGE_NAME%:%IMAGE_TAG%
                    docker logout
                    """
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                echo 'Deploying to Dev environment'
                // Example command to run your container on Dev server (adjust accordingly)
                bat """
                docker pull %IMAGE_NAME%:%IMAGE_TAG%
                docker stop carsaletwo-dev || echo No running container to stop
                docker rm carsaletwo-dev || echo No container to remove
                docker run -d -p 8081:8080 --name carsaletwo-dev %IMAGE_NAME%:%IMAGE_TAG%
                """
            }
        }

        stage('Deploy to Test') {
            steps {
                echo 'Deploying to Test environment'
                bat """
                docker pull %IMAGE_NAME%:%IMAGE_TAG%
                docker stop carsaletwo-test || echo No running container to stop
                docker rm carsaletwo-test || echo No container to remove
                docker run -d -p 8082:8080 --name carsaletwo-test %IMAGE_NAME%:%IMAGE_TAG%
                """
            }
        }

        stage('Deploy to Prod') {
            steps {
                echo 'Deploying to Prod environment'
                bat """
                docker pull %IMAGE_NAME%:%IMAGE_TAG%
                docker stop carsaletwo-prod || echo No running container to stop
                docker rm carsaletwo-prod || echo No container to remove
                docker run -d -p 8080:8080 --name carsaletwo-prod %IMAGE_NAME%:%IMAGE_TAG%
                """
            }
        }
    }
}
