pipeline {
    agent { label 'docker-agent' }

    tools {
        maven 'maven3'
    }

    options {
        buildDiscarder logRotator(
            daysToKeepStr: '15',
            numToKeepStr: '10'
        )
    }

    environment {
        APP_NAME      = "INTEGRACION_APP"
        APP_ENV       = "MAIN"
        SONARQUBE_ENV = "SonarQube25"  // Cambia por el nombre de tu SonarQube en Jenkins
    }

    stages {
        stage('Checkout Código') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/feature/integraciontest']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/henrymerino/integracion.git',
                        credentialsId: 'gitIntegracion' // Cambia esto por tu ID real
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
                // Espera a que termine el análisis y valida calidad
                timeout(time: 1, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Generar archivo en workspace') {
            steps {
                sh '''
                    echo "Este es un archivo de prueba del workspace" > workspace_test.txt
                    ls -la
                '''
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

        stage('Hacer merge a main') {
            steps {
                script {
                    sh '''
                        git config user.email "haguilarmerino@gmail.com"
                        git config user.name "henrymerino"

                        git checkout main
                        git pull origin main
                        git merge origin/feature/integraciontest --no-ff -m "Merge automático desde Jenkins"
                        git push origin main
                    '''
                }
            }
        }
    }
}
