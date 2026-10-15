pipeline {
    agent any

    stages {

        stage('Test') {
            steps {
                sh 'mvn -B test-compile org.apache.maven.plugins:maven-surefire-plugin:3.5.4:test -Dmaven.test.failure.ignore=true'
            }
            post {
                always {
                    junit allowEmptyResults: true,
                        testResults: 'target/surefire-reports/*.xml'
                }
            }
        }
    }
}