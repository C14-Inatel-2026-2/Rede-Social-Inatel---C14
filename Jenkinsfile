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

        stage('Build') {
            steps {
                echo 'Compilando o projeto '

                sh '''
                     java -version
                     mvn -version
                     mvn -B -DskipTests clean compile
                   '''
            }
        }
        stage('Análise estática') {
            steps {
                sh '''
                    mvn -B \
                      org.apache.maven.plugins:maven-checkstyle-plugin:3.6.0:checkstyle \
                      com.github.spotbugs:spotbugs-maven-plugin:4.10.5.0:spotbugs \
                      -Dcheckstyle.config.location=google_checks.xml \
                      -Dcheckstyle.failOnViolation=false
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
        stage('Deploy') {
            when{
                branch 'main'
            }
            steps{
                withCredentials([string(credentialsId: 'render-deploy-hook', variable: 'DEPLOY_HOOK')]){
                    sh 'curl -fsS -X POST "$DEPLOY_HOOK"'
                }
                echo 'Deploy disparado no render'
            }
        }
    }
}