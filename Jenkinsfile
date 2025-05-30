pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven'  // Your Maven installation name in Jenkins
        JDK_HOME = tool 'JDK17'    // Your JDK 17 installation name in Jenkins
        DOCKER_IMAGE = "emmalujoseph/carsaletwo"
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin", "JAVA_HOME=${JDK_HOME}"]) {
                    bat "mvn clean package"
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeScanner') { // your SonarQube installation name
                    withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin", "JAVA_HOME=${JDK_HOME}"]) {
                        bat "mvn sonar:sonar -Dsonar.projectKey=carsaletwo -Dsonar.sources=src/main/java -Dsonar.java.binaries=target/classes"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin", "JAVA_HOME=${JDK_HOME}"]) {
                    bat "mvn test"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    bat "docker login -u %DOCKER_USERNAME% -p %DOCKER_PASSWORD%"
                    bat "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    bat "docker logout"
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                echo "Deploying Docker image ${DOCKER_IMAGE}:${DOCKER_TAG} to Development environment"
                bat '''
                ssh devuser@dev-server "docker pull ${DOCKER_IMAGE}:${DOCKER_TAG} && docker stop carsaletwo || true && docker rm carsaletwo || true && docker run -d --name carsaletwo -p 8080:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}"
                '''
            }
        }

        stage('Deploy to Test') {
            steps {
                echo "Deploying Docker image ${DOCKER_IMAGE}:${DOCKER_TAG} to Test environment"
                bat '''
                ssh testuser@test-server "docker pull ${DOCKER_IMAGE}:${DOCKER_TAG} && docker stop carsaletwo || true && docker rm carsaletwo || true && docker run -d --name carsaletwo -p 8080:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}"
                '''
            }
        }

        stage('Deploy to Prod') {
            steps {
                input message: 'Approve deployment to Production environment?'
                echo "Deploying Docker image ${DOCKER_IMAGE}:${DOCKER_TAG} to Production environment"
                bat '''
                ssh produser@prod-server "docker pull ${DOCKER_IMAGE}:${DOCKER_TAG} && docker stop carsaletwo || true && docker rm carsaletwo || true && docker run -d --name carsaletwo -p 8080:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}"
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
