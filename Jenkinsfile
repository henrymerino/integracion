pipeline {
    agent { label 'docker-agent' }

    tools {
        maven 'maven3' // Asegúrate de que esté configurado en Jenkins
    }

    options {
        buildDiscarder logRotator(
            daysToKeepStr: '15',
            numToKeepStr: '10'
        )
    }

    environment {
        APP_NAME = "INTEGRACION_APP"
        APP_ENV  = "MAIN"
    }

    stages {
        stage('Checkout Código') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/feature/integraciontest']],
                    userRemoteConfigs: [[url: 'https://github.com/henrymerino/integracion.git']]
                ])
            }
        }

        stage('Compilación') {
            steps {
                sh 'mvn clean install -Dmaven.test.skip=true'
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
                // Archiva todos los .jar y el archivo de prueba
                archiveArtifacts artifacts: '**/target/*.jar, workspace_test.txt', fingerprint: true
            }
        }

        stage('Mostrar variables de entorno') {
            steps {
                sh 'env'
            }
        }
    }
}
