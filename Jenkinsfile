pipeline {
    agent any

    stages {

        stage('Source') {
            steps {
                echo 'Verificando o código-fonte obtido pelo Jenkins'

                sh '''
                    echo "Branch atual: ${BRANCH_NAME:-desconhecida}"
                    echo "Commit atual:"
                    git log -1 --oneline
            
                    echo "Arquivos na raiz do projeto:"
                    ls -la
                '''
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