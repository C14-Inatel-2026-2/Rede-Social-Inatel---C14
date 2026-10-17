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

        stage('Test') {
            steps {
                sh '''
                               mvn -B \
                                 org.jacoco:jacoco-maven-plugin:0.8.14:prepare-agent \
                                 test-compile \
                                 org.apache.maven.plugins:maven-surefire-plugin:3.5.4:test \
                                 org.jacoco:jacoco-maven-plugin:0.8.14:report \
                                 -DfailIfNoTests=false
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
        stage('Package') {
            steps {
                echo 'Gerando o artefato da aplicação'
                sh '''
                    mvn -B -DskipTests package
                    test -n "$(find target -maxdepth 1 -type f -name '*.jar' -print -quit)"
                '''
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

                  post {
                        success {
                            withCredentials([
                                string(
                                    credentialsId: 'discord-webhook',
                                    variable: 'DISCORD_WEBHOOK'
                                )
                            ]) {
                                sh '''
                                    curl -fsS -H "Content-Type: application/json" \
                                    -X POST \
                                    -d "{\"content\":\"✅ Sucesso: ${JOB_NAME} - Build #${BUILD_NUMBER}\"}" \
                                    "$DISCORD_WEBHOOK"
                                '''
                            }
                        }

                        failure {
                            withCredentials([
                                string(
                                    credentialsId: 'discord-webhook',
                                    variable: 'DISCORD_WEBHOOK'
                                )
                            ]) {
                                sh '''
                                    curl -fsS -H "Content-Type: application/json" \
                                    -X POST \
                                    -d "{\"content\":\"❌ Falha: ${JOB_NAME} - Build #${BUILD_NUMBER}\"}" \
                                    "$DISCORD_WEBHOOK"
                                '''
                            }
                        }
                    }


}