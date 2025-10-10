pipeline {
    agent { label 'docker-agent' }

    tools {
        maven 'maven3'
    }

    options {
        buildDiscarder logRotator(daysToKeepStr: '15', numToKeepStr: '10')
    }

    environment {
        APP_NAME      = "INTEGRACION_APP"
        APP_ENV       = "MAIN"
        SONARQUBE_ENV = "SonarQube25"  // Cambia esto si tu servidor SonarQube tiene otro nombre
         SLACK_WEBHOOK_URL = credentials('slackWebhook')
    }

    triggers {
        // Ejecuta el pipeline automáticamente cuando hay un push a Git
        pollSCM('* * * * *')  // Revisa cada minuto (puedes ajustar si lo deseas)
    }

    stages {
        stage('Checkout Código') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/feature/integraciontest']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/henrymerino/integracion.git',
                        credentialsId: 'gitIntegracion'
                    ]]
                ])
            }
        }

        stage('Compilación') {
            steps {
                sh 'mvn clean install -Dmaven.test.skip=true'
            }
        }

        stage('Análisis con SonarQube') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=integracion -Dsonar.host.url=http://host.docker.internal:9000'
                }
            }
        }

        stage('Esperar resultados de SonarQube') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Archivar artefactos') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar, workspace_test.txt', fingerprint: true
            }
        }

        stage('Mostrar variables de entorno') {
            steps {
                sh 'env'
            }
        }

        stage('Validar y hacer merge a main') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'gitIntegracion', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN')]) {
                    sh '''
                        git config user.name "$GIT_USER"
                        git config user.email "haguilarmerino@gmail.com"

                        git remote set-url origin https://$GIT_USER:$GIT_TOKEN@github.com/henrymerino/integracion.git

                        # Validar que el branch existe localmente
                        git fetch origin
                        git checkout main
                        git pull origin main

                        git merge origin/feature/integraciontest --no-ff -m "Merge automático desde Jenkins" || exit 1

                        git push origin main
                    '''
                }
            }
        }
    }
}

// 👇 Notificaciones de Slack al final del pipeline
    post {
        success {
            slackNotify("✅ *Pipeline exitoso* `${env.JOB_NAME}` #${env.BUILD_NUMBER} - <${env.BUILD_URL}|Ver detalles>")
        }
        failure {
            slackNotify("❌ *Pipeline fallido* `${env.JOB_NAME}` #${env.BUILD_NUMBER} - <${env.BUILD_URL}|Ver detalles>")
        }
    }
}

// 👇 Función para enviar mensajes a Slack
def slackNotify(String message) {
    sh """
        curl -X POST -H 'Content-type: application/json' \
        --data '{ "text": "${message}" }' \
        ${SLACK_WEBHOOK_URL}
    """
}
