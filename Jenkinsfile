pipeline {
    agent { label 'docker-agent' }

    tools {
        maven 'maven3'
    }

    options {
        buildDiscarder logRotator(daysToKeepStr: '15', numToKeepStr: '10')
        timestamps() // útil para debug
    }

    environment {
        APP_NAME      = "INTEGRACION_APP"
        APP_ENV       = "MAIN"
        SONARQUBE_ENV = "SonarQube25"  // Asegúrate que coincide con el nombre en Jenkins
        GIT_EMAIL     = "haguilarmerino@gmail.com" // 👈 puedes ajustar este valor
    }

    triggers {
        // Ejecuta el pipeline automáticamente cuando hay un push a Git
        pollSCM('H/5 * * * *')  // Revisa cada 5 minutos de forma distribuida
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
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar ' +
                       '-Dsonar.projectKey=integracion ' +
                       '-Dsonar.host.url=http://host.docker.internal:9000'
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
                        git config user.email "${GIT_EMAIL}"

                        git remote set-url origin https://$GIT_USER:$GIT_TOKEN@github.com/henrymerino/integracion.git

                        git fetch origin
                        git checkout main
                        git pull origin main

                        git merge origin/feature/integraciontest --no-ff -m "Merge automático desde Jenkins" || exit 1

                        git log -1 --oneline  # Mostrar el último commit mergeado
                        git push origin main
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completado exitosamente.'
        }
        failure {
            echo '❌ El pipeline falló. Revisa los logs.'
        }
        always {
            echo '📝 Pipeline finalizado (éxito o fallo).'
        }
    }
}
