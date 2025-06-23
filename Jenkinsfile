pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'blog-app'
        DOCKER_TAG = "${BUILD_NUMBER}"
        MAVEN_HOME = '/opt/maven'
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-amd64'
    }

    tools {
        maven 'Maven-3.8.6'
        jdk 'JDK-11'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
                sh 'ls -la'
            }
        }

        stage('Build') {
            steps {
                echo 'Building application...'
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging application...'
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Code Quality') {
            steps {
                echo 'Running code quality checks...'
                sh 'mvn verify -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                script {
                    def image = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.withRegistry('', '') {
                        image.push()
                        image.push('latest')
                    }
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                echo 'Deploying application...'
                script {
                    sh '''
                        cd ansible/playbooks
                        ansible-playbook -i ../inventory/hosts deploy.yml \
                        --extra-vars "app_version=${DOCKER_TAG}"
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                echo 'Performing health check...'
                sh '''
                    sleep 30
                    curl -f http://localhost:8080/actuator/health || exit 1
                    curl -f http://localhost:8080/api/blog/posts || exit 1
                '''
            }
        }

        stage('Monitoring Setup') {
            steps {
                echo 'Restarting monitoring stack...'
                sh '''
                    cd ~/monitoring
                    docker-compose restart grafana
                '''
            }
        }
    }

    post {
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
