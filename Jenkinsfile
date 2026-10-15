pipeline {
    agent any

    stages {

        stage('Test') {
            steps {
                sh 'mvn -B test'
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