pipeline {
    agent any

    stages {

        stage('Source') {
            steps {
                git branch: 'main',
                        url: 'https://github.com/C14-Inatel-2026-2/Rede-Social-Inatel---C14.git'
            }
        }

        stage('Test') {
            steps {
                sh '''
                               mvn -B \
                                 org.jacoco:jacoco-maven-plugin:0.8.14:prepare-agent \
                                 test-compile \
                                 org.apache.maven.plugins:maven-surefire-plugin:3.5.4:test \
                                 org.jacoco:jacoco-maven-plugin:0.8.14:report \
                                 -Dmaven.test.failure.ignore=true
                           '''
            }
            post {
                always {
                    junit allowEmptyResults: true,
                        testResults: 'target/surefire-reports/*.xml'
                    recordCoverage tools: [[parser: 'JACOCO',
                                                        pattern: 'target/site/jacoco/jacoco.xml']]
                }
            }
        }
    }
}